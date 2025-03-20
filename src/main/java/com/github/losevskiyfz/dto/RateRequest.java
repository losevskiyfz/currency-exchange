package com.github.losevskiyfz.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
public class RateRequest {
    @NotNull
    @Positive
    private String rate;
}
