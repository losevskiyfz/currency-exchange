package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.exception.ExchangeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.github.losevskiyfz.utils.CurrencyUtils.convertAmount;

public class ExchangeServiceImpl implements ExchangeService {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);

    @Override
    public ExchangeDto exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) {
        try {
            ExchangeRateDto exchangeRateDto = exchangeRateService.getExchangeRateBySourceAndTargetCode(baseCurrencyCode, targetCurrencyCode);
            return ExchangeDto.builder()
                    .baseCurrency(exchangeRateDto.getBaseCurrency())
                    .targetCurrency(exchangeRateDto.getTargetCurrency())
                    .rate(exchangeRateDto.getRate())
                    .amount(new BigDecimal(amount))
                    .convertedAmount(convertAmount(exchangeRateDto.getRate(), amount))
                    .build();
        } catch (Exception ignored) {
        }
        try {
            ExchangeRateDto exchangeRateDto = exchangeRateService.getExchangeRateBySourceAndTargetCode(targetCurrencyCode, baseCurrencyCode);
            return ExchangeDto.builder()
                    .baseCurrency(exchangeRateDto.getBaseCurrency())
                    .targetCurrency(exchangeRateDto.getTargetCurrency())
                    .rate(BigDecimal.ONE.divide(exchangeRateDto.getRate(), RoundingMode.DOWN))
                    .amount(new BigDecimal(amount))
                    .convertedAmount(convertAmount(exchangeRateDto.getRate(), amount))
                    .build();
        } catch (Exception ignored) {
        }
        try {
            ExchangeRateDto exchangeRateDtoUsdBase = exchangeRateService.getExchangeRateBySourceAndTargetCode("USD", baseCurrencyCode);
            ExchangeRateDto exchangeRateDtoUsdTarget = exchangeRateService.getExchangeRateBySourceAndTargetCode("USD", targetCurrencyCode);
            BigDecimal rate = exchangeRateDtoUsdBase.getRate().divide(exchangeRateDtoUsdTarget.getRate(), RoundingMode.DOWN);
            return ExchangeDto.builder()
                    .baseCurrency(exchangeRateDtoUsdBase.getTargetCurrency())
                    .targetCurrency(exchangeRateDtoUsdTarget.getTargetCurrency())
                    .rate(rate)
                    .amount(new BigDecimal(amount))
                    .convertedAmount(convertAmount(rate, amount))
                    .build();
        } catch (Exception ignored){
        }
        throw new ExchangeException("There is no exchange rate for this currency pair");
    }
}