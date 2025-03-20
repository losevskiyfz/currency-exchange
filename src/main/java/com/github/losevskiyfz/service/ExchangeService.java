package com.github.losevskiyfz.service;

import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.CurrencyUtils.convertAmount;

public class ExchangeService {
    private static final Logger logger = Logger.getLogger(ExchangeService.class.getName());
    private static final int FLOATING_POINT_SCALE = 6;

    public ExchangeDto exchangeDirect(ExchangeRateDto exchangeRateDto, String amount) {
        BigDecimal rate = exchangeRateDto.getRate();
        return ExchangeDto.builder()
                .baseCurrency(exchangeRateDto.getBaseCurrency())
                .targetCurrency(exchangeRateDto.getTargetCurrency())
                .rate(rate)
                .amount(new BigDecimal(amount))
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }

    public ExchangeDto exchangeReverse(ExchangeRateDto exchangeRateDto, String amount) {
        BigDecimal rate = BigDecimal.ONE.divide(exchangeRateDto.getRate(), FLOATING_POINT_SCALE, RoundingMode.HALF_UP);
        return ExchangeDto.builder()
                .baseCurrency(exchangeRateDto.getTargetCurrency())
                .targetCurrency(exchangeRateDto.getBaseCurrency())
                .rate(rate)
                .amount(new BigDecimal(amount))
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }

    private void addExchangeRates(List<ExchangeRateDto> exchangeRateDtos, Map<String, Map<String, BigDecimal>> exchangeGraph) {
        exchangeRateDtos.forEach(er -> addExchangeRate(er, exchangeGraph));
    }

    private void addExchangeRate(ExchangeRateDto exchange, Map<String, Map<String, BigDecimal>> exchangeGraph) {
        exchangeGraph
                .computeIfAbsent(exchange.getBaseCurrency().getCode(), k -> new HashMap<>())
                .put(exchange.getTargetCurrency().getCode(), exchange.getRate());

        exchangeGraph
                .computeIfAbsent(exchange.getTargetCurrency().getCode(), k -> new HashMap<>())
                .put(exchange.getBaseCurrency().getCode(), BigDecimal.ONE.divide(exchange.getRate(), 6, RoundingMode.HALF_UP));
    }

    public Optional<ExchangeDto> convert(List<ExchangeRateDto> exchangeRateDtos, CurrencyDto from, CurrencyDto to, BigDecimal amount) {
        Map<String, Map<String, BigDecimal>> exchangeGraph = new HashMap<>();
        addExchangeRates(exchangeRateDtos, exchangeGraph);
        if (!exchangeGraph.containsKey(from.getCode()) || !exchangeGraph.containsKey(to.getCode())) {
            logger.warning("No exchange path found between " + from + " and " + to);
            return Optional.empty();
        }

        Map<String, BigDecimal> rates = new HashMap<>();
        Set<String> visited = new HashSet<>();
        rates.put(from.getCode(), BigDecimal.ONE);
        PriorityQueue<CurrencyNode> pq = new PriorityQueue<>(Comparator.comparing(CurrencyNode::getRate).reversed());
        pq.add(new CurrencyNode(from.getCode(), BigDecimal.ONE));

        int maxIterations = exchangeGraph.size() * 2;
        int iterations = 0;

        while (!pq.isEmpty()) {
            if (++iterations > maxIterations) {
                logger.warning("Potential infinite loop detected in currency conversion.");
                return Optional.empty();
            }

            CurrencyNode current = pq.poll();
            String currency = current.currency;
            BigDecimal rate = current.rate;

            if (currency.equals(to.getCode())) {
                return Optional.of(ExchangeDto.builder()
                        .baseCurrency(from)
                        .targetCurrency(to)
                        .amount(amount)
                        .rate(rate)
                        .convertedAmount(convertAmount(rate, amount))
                        .build());
            }

            if (!visited.add(currency)) continue; // Prevent cycles

            for (Map.Entry<String, BigDecimal> entry : exchangeGraph.getOrDefault(currency, Collections.emptyMap()).entrySet()) {
                String neighbor = entry.getKey();
                BigDecimal newRate = rate.multiply(entry.getValue());

                if (!rates.containsKey(neighbor) || newRate.compareTo(rates.get(neighbor)) > 0) {
                    rates.put(neighbor, newRate);
                    pq.add(new CurrencyNode(neighbor, newRate));
                }
            }
        }

        logger.warning("No valid exchange path found.");
        return Optional.empty();
    }

    static class CurrencyNode {
        String currency;
        @Getter
        BigDecimal rate;

        public CurrencyNode(String currency, BigDecimal rate) {
            this.currency = currency;
            this.rate = rate;
        }
    }
}
