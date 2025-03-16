package com.github.losevskiyfz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ExchangeRate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "base_currency_id", referencedColumnName = "ID")
    private Currency baseCurrency;

    @ManyToOne
    @JoinColumn(name = "target_currency_id", referencedColumnName = "ID")
    private Currency targetCurrency;

    @Column(name = "rate")
    private BigDecimal rate;
}
