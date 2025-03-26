package com.github.losevskiyfz.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ExchangeRequest {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
