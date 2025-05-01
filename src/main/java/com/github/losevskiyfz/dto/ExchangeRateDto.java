package com.github.losevskiyfz.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class ExchangeRateDto {
    private Long id;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
}