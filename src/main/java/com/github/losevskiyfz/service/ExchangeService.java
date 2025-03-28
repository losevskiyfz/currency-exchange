package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.CurrencyUtils.convertAmount;

public class ExchangeService {
    private static final Logger logger = Logger.getLogger(ExchangeService.class.getName());
    private static final int FLOATING_POINT_SCALE = 64;
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
        Optional<ExchangeRateDto> usdSource = exchangeRates.stream()
                .filter(
                        e -> e.getBaseCurrency().getCode().equals("USD") &&
                                e.getTargetCurrency().getCode().equals(baseCurrencyCode)
                ).findFirst();
        Optional<ExchangeRateDto> usdTarget = exchangeRates.stream()
                .filter(
                        e -> e.getBaseCurrency().getCode().equals("USD") &&
                                e.getTargetCurrency().getCode().equals(targetCurrencyCode)
                ).findFirst();
        if (usdSource.isPresent() && usdTarget.isPresent()) {
            BigDecimal rate = usdSource.get().getRate().divide(usdTarget.get().getRate(), RoundingMode.HALF_UP);
            return Optional.of(
                    ExchangeDto.builder()
                            .baseCurrency(usdSource.get().getTargetCurrency())
                            .targetCurrency(usdTarget.get().getTargetCurrency())
                            .rate(rate)
                            .amount(new BigDecimal(amount))
                            .convertedAmount(convertAmount(rate, amount))
                            .build()
            );
        } else {
            return Optional.empty();
        }
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
        BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), FLOATING_POINT_SCALE, RoundingMode.UP);
        return ExchangeDto.builder()
                .baseCurrency(exchangeRate.getTargetCurrency())
                .targetCurrency(exchangeRate.getBaseCurrency())
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertAmount(rate, amount))
                .build();
    }
}
