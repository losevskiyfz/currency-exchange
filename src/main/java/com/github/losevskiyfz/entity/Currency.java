package com.github.losevskiyfz.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    private Integer id;
    private String fullName;
    private String code;
    private String sign;
}