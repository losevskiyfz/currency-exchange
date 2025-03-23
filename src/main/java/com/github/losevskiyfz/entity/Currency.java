package com.github.losevskiyfz.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Currency {
    private Long id;
    private String fullName;
    private String code;
    private String sign;
}
