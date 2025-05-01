package com.github.losevskiyfz.dto;

import lombok.*;

@Setter
@Getter
@Builder
public class CurrencyDto {
    private Integer id;
    private String name;
    private String code;
    private String sign;
}