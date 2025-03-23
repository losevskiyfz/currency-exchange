package com.github.losevskiyfz.dto.validator;

import com.github.losevskiyfz.dto.PatchExchangeRate;
import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.dto.PostExchangeRate;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class Validator {
    private static final Logger logger = Logger.getLogger(Validator.class.getName());

    public void validate(Object object) {
        logger.info("Validating object " + object);
        if (object instanceof PostCurrency) {
            validatePostCurrency((PostCurrency) object);
        } else if (object instanceof PostExchangeRate) {
            validatePostExchangeRate((PostExchangeRate) object);
        } else if (object instanceof PatchExchangeRate) {
            validatePatchExchangeRate((PatchExchangeRate) object);
        }
        logger.info("Validated object " + object + " successfully");
    }

    void validatePostCurrency(PostCurrency postCurrency) {
        if (postCurrency.getCode() == null ||
                postCurrency.getCode().length() != 3 ||
                postCurrency.getName() == null ||
                postCurrency.getSign() == null
        ) {
            logger.info("Invalid posted currency " + postCurrency);
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
            logger.info("Invalid posted exchange rate " + exchangeRateDto);
            throw new RuntimeException();
        }
    }

    void validatePatchExchangeRate(PatchExchangeRate exchangeRateDto) {
        if (exchangeRateDto.getRate() == null ||
                exchangeRateDto.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("Invalid exchange rate to patch " + exchangeRateDto);
            throw new RuntimeException();
        }
    }
}
