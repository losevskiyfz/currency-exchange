package com.github.losevskiyfz.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ExchangeRate {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}