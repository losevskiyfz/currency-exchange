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
@Table(name = "exchange_rate")
public class ExchangeRate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "base_currency_id", referencedColumnName = "id")
    private Currency baseCurrency;

    @ManyToOne
    @JoinColumn(name = "target_currency_id", referencedColumnName = "id")
    private Currency targetCurrency;

    @Column(name = "rate")
    private BigDecimal rate;

}
