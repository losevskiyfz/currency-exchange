package com.github.losevskiyfz.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class PostExchangeRate {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private String rate;
}