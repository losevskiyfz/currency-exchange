package com.github.losevskiyfz.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CurrencyDto {
    private Long id;
    private String name;
    private String code;
    private String sign;
}
