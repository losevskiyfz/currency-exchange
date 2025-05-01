package com.github.losevskiyfz.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ExchangeRequest {
    String baseCurrencyCode;
    String targetCurrencyCode;
    String amount;
}
