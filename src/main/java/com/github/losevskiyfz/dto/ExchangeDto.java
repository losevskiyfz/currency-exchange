package com.github.losevskiyfz.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ExchangeDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}