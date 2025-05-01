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

    public static BigDecimal convertAmount(BigDecimal rate, BigDecimal amount) {
        return amount.multiply(rate);
    }

    public static BigDecimal convertAmount(String rate, String amount) {
        return convertAmount(new BigDecimal(rate), new BigDecimal(amount));
    }

    public static BigDecimal convertAmount(BigDecimal rate, String amount) {
        return convertAmount(rate, new BigDecimal(amount));
    }

    public static BigDecimal convertAmount(String rate, BigDecimal amount) {
        return convertAmount(new BigDecimal(rate), amount);
    }
}