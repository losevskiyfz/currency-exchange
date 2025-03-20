package com.github.losevskiyfz.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class ExchangeRateDto {
    @JsonIgnore
    private static int idGen = 1;
    private Integer id = idGen++;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    @NotNull
    @Positive
    private BigDecimal rate;
}
