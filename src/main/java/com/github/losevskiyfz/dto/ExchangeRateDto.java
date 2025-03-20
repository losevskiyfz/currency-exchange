package com.github.losevskiyfz.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class ExchangeRateDto {
    private Integer id;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
}
