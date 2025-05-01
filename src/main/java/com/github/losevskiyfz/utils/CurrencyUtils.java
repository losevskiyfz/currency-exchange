package com.github.losevskiyfz.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtils {
    public static BigDecimal round(BigDecimal value, int scale) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return value.setScale(scale, RoundingMode.UP);
    }
}