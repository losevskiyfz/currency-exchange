package com.github.losevskiyfz.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class ExchangeDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    @Positive
    @NotNull
    private BigDecimal rate;
    @Positive
    @NotNull
    private BigDecimal amount;
    @Positive
    @NotNull
    private BigDecimal convertedAmount;
}
