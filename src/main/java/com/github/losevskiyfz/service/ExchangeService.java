package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.CurrencyUtils.convertAmount;

public class ExchangeService {
    private static final Logger logger = Logger.getLogger(ExchangeService.class.getName());
    private static final int FLOATING_POINT_SCALE = 10;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);

    public Optional<ExchangeDto> exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) throws SQLException, InterruptedException {
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAll();
        Optional<ExchangeRateDto> directExchange = exchangeRates.stream()
                .filter(
                        e -> e.getBaseCurrency().getCode().equals(baseCurrencyCode) &&
                                e.getTargetCurrency().getCode().equals(targetCurrencyCode)
                ).findFirst();
        if (directExchange.isPresent()) {
            return Optional.of(exchangeDirect(directExchange.get(), new BigDecimal(amount)));
        }
        Optional<ExchangeRateDto> reverseExchange = exchangeRates.stream()
                .filter(
                        e -> e.getBaseCurrency().getCode().equals(targetCurrencyCode) &&
                                e.getTargetCurrency().getCode().equals(baseCurrencyCode)
                ).findFirst();
        if (reverseExchange.isPresent()) {
            return Optional.of(exchangeReverse(reverseExchange.get(), new BigDecimal(amount)));
        }
        return exchangeCross(exchangeRates, baseCurrencyCode, targetCurrencyCode, new BigDecimal(amount));
    }

    private ExchangeDto exchangeDirect(ExchangeRateDto exchangeRate, BigDecimal amount) {
        BigDecimal rate = exchangeRate.getRate();
        return ExchangeDto.builder()
                .baseCurrency(exchangeRate.getBaseCurrency())
                .targetCurrency(exchangeRate.getTargetCurrency())
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }

    private ExchangeDto exchangeReverse(ExchangeRateDto exchangeRate, BigDecimal amount) {
        BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), FLOATING_POINT_SCALE, RoundingMode.HALF_UP);
        return ExchangeDto.builder()
                .baseCurrency(exchangeRate.getTargetCurrency())
                .targetCurrency(exchangeRate.getBaseCurrency())
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }

    public Optional<ExchangeDto> exchangeCross(List<ExchangeRateDto> exchangeRates, String baseCode, String targetCode, BigDecimal amount) {
        return ExchangeRateFinder.convertCurrency(exchangeRates, baseCode, targetCode, amount);
    }

    private static class ExchangeRateFinder {

        private static void addExchangeRate(CurrencyDto base, CurrencyDto target, BigDecimal fromRate, BigDecimal toRate, Graph<CurrencyDto, DefaultWeightedEdge> graph) {
            graph.addVertex(base);
            graph.addVertex(target);

            if (!"-1".equals(fromRate)) {
                addOrUpdateEdge(base, target, fromRate, graph);
            }

            if (!"-1".equals(toRate)) {
                addOrUpdateEdge(target, base, toRate, graph);
            }
        }

        private static void addOrUpdateEdge(CurrencyDto from, CurrencyDto to, BigDecimal rate, Graph<CurrencyDto, DefaultWeightedEdge> graph) {
            double newWeight = -Math.log(rate.doubleValue());
            DefaultWeightedEdge edge = graph.getEdge(from, to);

            if (edge == null) {
                edge = graph.addEdge(from, to);
                graph.setEdgeWeight(edge, newWeight);
            } else {
                double currentWeight = graph.getEdgeWeight(edge);
                if (newWeight < currentWeight) {
                    graph.setEdgeWeight(edge, newWeight);
                }
            }
        }

        public static Set<CurrencyDto> extractCurrencies(List<ExchangeRateDto> exchangeRates){
            Set<CurrencyDto> currencies = new HashSet<>();
            for (ExchangeRateDto exchangeRate : exchangeRates) {
                currencies.add(exchangeRate.getBaseCurrency());
                currencies.add(exchangeRate.getTargetCurrency());
            }
            return currencies;
        }

        public static Optional<ExchangeDto> convertCurrency(List<ExchangeRateDto> exchangeRates, String from, String to, BigDecimal amount) {
            Graph<CurrencyDto, DefaultWeightedEdge> graph =
                    new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            for (ExchangeRateDto exchangeRate : exchangeRates) {
                addExchangeRate(
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate(),
                        BigDecimal.ONE.divide(exchangeRate.getRate(), RoundingMode.HALF_UP),
                        graph
                );
            }

            Set<CurrencyDto> currencies = extractCurrencies(exchangeRates);

            Optional<CurrencyDto> fromCurrency = currencies.stream().filter(c -> c.getCode().equals(from)).findFirst();
            Optional<CurrencyDto> toCurrency = currencies.stream().filter(c -> c.getCode().equals(to)).findFirst();

            if (fromCurrency.isEmpty() || toCurrency.isEmpty()) {
                return Optional.empty();
            }

            DijkstraShortestPath<CurrencyDto, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
            GraphPath<CurrencyDto, DefaultWeightedEdge> path = dijkstra.getPath(fromCurrency.get(), toCurrency.get());

            if (path == null) {
                return Optional.empty();
            }

            BigDecimal conversionRate = BigDecimal.ONE;
            for (int i = 0; i < path.getVertexList().size() - 1; i++) {
                CurrencyDto current = path.getVertexList().get(i);
                CurrencyDto next = path.getVertexList().get(i + 1);
                DefaultWeightedEdge edge = graph.getEdge(current, next);
                double edgeWeight = graph.getEdgeWeight(edge);
                BigDecimal rate = BigDecimal.valueOf(Math.exp(-edgeWeight));
                conversionRate = conversionRate.multiply(rate);
            }

            BigDecimal convertedAmount = amount.multiply(conversionRate);
            return Optional.of(ExchangeDto.builder()
                    .baseCurrency(fromCurrency.get())
                    .targetCurrency(toCurrency.get())
                    .amount(amount)
                    .rate(conversionRate)
                    .convertedAmount(convertedAmount)
                    .build());
        }
    }
}
