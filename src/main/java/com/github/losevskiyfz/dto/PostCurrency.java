package com.github.losevskiyfz.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCurrency {
    private String name;
    private String code;
    private String sign;
}
