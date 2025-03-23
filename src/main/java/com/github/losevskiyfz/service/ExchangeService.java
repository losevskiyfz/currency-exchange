package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.Exchange;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.CurrencyUtils.convertAmount;

public class ExchangeService {
    private static final Logger logger = Logger.getLogger(ExchangeService.class.getName());
    private static final int FLOATING_POINT_SCALE = 6;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);

    public Optional<Exchange> exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) throws SQLException, InterruptedException {
        Optional<ExchangeRateDto> directExchangeRateDto = exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);
        if (directExchangeRateDto.isPresent()) {
            return Optional.of(exchangeDirect(directExchangeRateDto.get(), new BigDecimal(amount)));
        }
        Optional<ExchangeRateDto> reversedExchangeRate = exchangeRateService.getByCodes(targetCurrencyCode, baseCurrencyCode);
        if (reversedExchangeRate.isPresent()) {
            return Optional.of(exchangeReverse(reversedExchangeRate.get(), new BigDecimal(amount)));
        }
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAll();
        return exchangeCross(exchangeRates, baseCurrencyCode, targetCurrencyCode, new BigDecimal(amount));
    }

    private Exchange exchangeDirect(ExchangeRateDto exchangeRate, BigDecimal amount) {
        BigDecimal rate = exchangeRate.getRate();
        return Exchange.builder()
                .baseCurrency(exchangeRate.getBaseCurrency())
                .targetCurrency(exchangeRate.getTargetCurrency())
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }

    private Exchange exchangeReverse(ExchangeRateDto exchangeRate, BigDecimal amount) {
        BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), FLOATING_POINT_SCALE, RoundingMode.HALF_UP);
        return Exchange.builder()
                .baseCurrency(exchangeRate.getTargetCurrency())
                .targetCurrency(exchangeRate.getBaseCurrency())
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }

    private Optional<Exchange> exchangeCross(List<ExchangeRateDto> exchangeRates, String baseCode, String targetCode, BigDecimal amount) {
        Map<String, Map<String, BigDecimal>> exchangeGraph = new HashMap<>();
        addExchangeRates(exchangeRates, exchangeGraph);
        if (!exchangeGraph.containsKey(baseCode) || !exchangeGraph.containsKey(targetCode)) {
            logger.warning("No exchange path found between " + baseCode + " and " + targetCode);
            return Optional.empty();
        }
        Map<String, BigDecimal> rates = new HashMap<>();
        Set<String> visited = new HashSet<>();
        rates.put(baseCode, BigDecimal.ONE);
        PriorityQueue<CurrencyNode> pq = new PriorityQueue<>(Comparator.comparing(CurrencyNode::getRate).reversed());
        pq.add(new CurrencyNode(baseCode, BigDecimal.ONE));
        int maxIterations = exchangeGraph.size() * 2;
        int iterations = 0;
        while (!pq.isEmpty()) {
            if (++iterations > maxIterations) {
                return Optional.empty();
            }
            CurrencyNode current = pq.poll();
            String currency = current.currency;
            BigDecimal rate = current.rate;
            Set<CurrencyDto> currencies = new HashSet<>();
            exchangeRates.forEach(er -> {
                currencies.add(er.getBaseCurrency());
                currencies.add(er.getTargetCurrency());
            });
            if (currency.equals(targetCode)) {
                return Optional.of(Exchange.builder()
                        .baseCurrency(
                                currencies.stream()
                                        .filter(c -> c.getCode().equals(baseCode))
                                        .findFirst().orElseThrow()
                        )
                        .targetCurrency(
                                currencies.stream()
                                        .filter(c -> c.getCode().equals(targetCode))
                                        .findFirst().orElseThrow()
                        )
                        .amount(amount)
                        .rate(rate)
                        .convertedAmount(convertAmount(rate, amount))
                        .build());
            }
            if (!visited.add(currency)) continue;
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

    private void addExchangeRates(List<ExchangeRateDto> exchangeRates, Map<String, Map<String, BigDecimal>> exchangeGraph) {
        exchangeRates.forEach(er -> addExchangeRate(er, exchangeGraph));
    }

    private void addExchangeRate(ExchangeRateDto exchange, Map<String, Map<String, BigDecimal>> exchangeGraph) {
        exchangeGraph
                .computeIfAbsent(exchange.getBaseCurrency().getCode(), k -> new HashMap<>())
                .put(exchange.getTargetCurrency().getCode(), exchange.getRate());
        exchangeGraph
                .computeIfAbsent(exchange.getTargetCurrency().getCode(), k -> new HashMap<>())
                .put(exchange.getBaseCurrency().getCode(), BigDecimal.ONE.divide(exchange.getRate(), 6, RoundingMode.HALF_UP));
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
