package com.github.losevskiyfz.dto.validator;

import com.github.losevskiyfz.dto.PatchExchangeRate;
import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.dto.PostExchangeRate;

import java.math.BigDecimal;

public class Validator {
    public void validate(Object object) {
        if (object instanceof PostCurrency) {
            validatePostCurrency((PostCurrency) object);
        } else if (object instanceof PostExchangeRate) {
            validatePostExchangeRate((PostExchangeRate) object);
        } else if (object instanceof PatchExchangeRate) {
            validatePatchExchangeRate((PatchExchangeRate) object);
        }
    }

    void validatePostCurrency(PostCurrency postCurrency) {
        if (postCurrency.getCode() == null ||
                postCurrency.getCode().length() != 3 ||
                postCurrency.getName() == null ||
                postCurrency.getSign() == null
        ) {
            throw new RuntimeException();
        }
    }

    void validatePostExchangeRate(PostExchangeRate exchangeRateDto) {
        if (
                exchangeRateDto.getRate() == null ||
                        exchangeRateDto.getRate().compareTo(BigDecimal.ZERO) <= 0 ||
                        exchangeRateDto.getBaseCurrencyCode() == null ||
                        exchangeRateDto.getBaseCurrencyCode().length() != 3 ||
                        exchangeRateDto.getTargetCurrencyCode() == null ||
                        exchangeRateDto.getTargetCurrencyCode().length() != 3
        ) {
            throw new RuntimeException();
        }
    }

    void validatePatchExchangeRate(PatchExchangeRate exchangeRateDto) {
        if (exchangeRateDto.getRate() == null ||
                exchangeRateDto.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException();
        }
    }
}
