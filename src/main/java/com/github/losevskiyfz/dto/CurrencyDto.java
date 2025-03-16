package com.github.losevskiyfz.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CurrencyDto {
    private String id;
    private String name;
    private String code;
    private String sign;

}
