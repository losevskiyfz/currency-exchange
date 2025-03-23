package com.github.losevskiyfz.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostExchangeRate {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
