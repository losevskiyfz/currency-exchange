package com.github.losevskiyfz.validation;

import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.dto.PostExchangeRate;
import com.github.losevskiyfz.exception.InvalidPathInfoException;
import com.github.losevskiyfz.exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import static com.github.losevskiyfz.conf.AllowedCodesProvider.getAllowedCodes;

public class Validator {
    private static final List<String> ALLOWED_CODES = getAllowedCodes();
    private static final Logger LOG = Logger.getLogger(Validator.class.getName());

    public void validate(Object object) {
        if (object instanceof String){
            validateCode((String) object);
        } else if (object instanceof PostCurrency) {
            validatePostCurrency((PostCurrency) object);
        } else if (object instanceof PostExchangeRate){
            validatePostExchangeRate((PostExchangeRate) object);
        }
    }

    private void validatePostExchangeRate(PostExchangeRate postExchangeRate) {
        if (postExchangeRate.getRate() == null || postExchangeRate.getRate().isEmpty()) throw new ValidationException("Rate is required");
        if (postExchangeRate.getRate().length() > 64)
            throw new ValidationException("Rate value is too long");
        if (!postExchangeRate.getRate().matches("\\d+(\\.\\d+)?"))
            throw new ValidationException("Rate must be a number");
        if (new BigDecimal(postExchangeRate.getRate()).compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Rate must be greater than 0");
        if (postExchangeRate.getBaseCurrencyCode() == null || postExchangeRate.getBaseCurrencyCode().isEmpty())
            throw new ValidationException("Base code is required");
        if (!ALLOWED_CODES.contains(postExchangeRate.getBaseCurrencyCode()))
            throw new ValidationException(String.format("Code: %s. Code does not meet ISO-4217", postExchangeRate.getBaseCurrencyCode()));
        if (postExchangeRate.getTargetCurrencyCode() == null || postExchangeRate.getTargetCurrencyCode().isEmpty())
            throw new ValidationException("Target code is required");
        if (!ALLOWED_CODES.contains(postExchangeRate.getTargetCurrencyCode()))
            throw new ValidationException(String.format("Code: %s. Code does not meet ISO-4217", postExchangeRate.getTargetCurrencyCode()));
    }

    private void validateCode(String code) {
        if (!ALLOWED_CODES.contains(code)) {
            throw new InvalidPathInfoException(String.format("Code: %s. Code does not meet ISO-4217", code));
        }
    }

    private void validatePostCurrency(PostCurrency postCurrency) {
        LOG.info("Validating PostCurrency: " + postCurrency);
        if(postCurrency.getCode() == null || postCurrency.getCode().isEmpty()) throw new ValidationException("Code is required");
        if(postCurrency.getName() == null || postCurrency.getName().isEmpty()) throw new ValidationException("Name is required");
        if(postCurrency.getSign() == null || postCurrency.getSign().isEmpty()) throw new ValidationException("Sign is required");
        if(postCurrency.getName().length() > 32) throw new ValidationException("Name is too long");
        if(postCurrency.getSign().length() > 8) throw new ValidationException("Sign is too long");
        if(!ALLOWED_CODES.contains(postCurrency.getCode())) throw new ValidationException("Code does not meet ISO-4217");
    }
}
