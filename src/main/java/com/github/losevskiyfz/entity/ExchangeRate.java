package com.github.losevskiyfz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "ExchangeRates")
public class ExchangeRate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BaseCurrencyId", referencedColumnName = "ID")
    private Currency baseCurrency;

    @ManyToOne
    @JoinColumn(name = "TargetCurrencyId", referencedColumnName = "ID")
    private Currency targetCurrency;

    @Column(name = "Rate")
    private BigDecimal rate;

}
