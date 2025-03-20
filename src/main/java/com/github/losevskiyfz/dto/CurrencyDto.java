package com.github.losevskiyfz.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class CurrencyDto {
    @JsonIgnore
    private static int idGen = 1;
    private Integer id = idGen++;
    private String name;
    @NotNull
    @Size(min = 3, max = 3)
    private String code;
    private String sign;

}
