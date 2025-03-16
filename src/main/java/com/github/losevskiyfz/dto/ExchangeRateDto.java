package com.github.losevskiyfz.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ExchangeRateDto {
    private String id;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private String rate;
}
