package com.github.losevskiyfz.utils;

import java.math.BigDecimal;

public class CurrencyUtils {
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