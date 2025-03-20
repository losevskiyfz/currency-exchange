package com.github.losevskiyfz.utils;

import java.math.BigDecimal;

public class CurrencyConverter {
    public static String convertAmount(String rate, String amount) {
        BigDecimal amountDecimal = new BigDecimal(amount);
        BigDecimal res = amountDecimal.multiply(new BigDecimal(rate));
        return res.toString();
    }
}
