package com.github.losevskiyfz.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
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
