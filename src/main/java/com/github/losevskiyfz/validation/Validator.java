package com.github.losevskiyfz.validation;

import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.exception.ValidationException;

import java.util.List;
import java.util.logging.Logger;

import static com.github.losevskiyfz.conf.AllowedCodesProvider.getAllowedCodes;

public class Validator {
    private static final List<String> ALLOWED_CODES = getAllowedCodes();
    private static final Logger LOG = Logger.getLogger(Validator.class.getName());

    public void validate(Object object) {
        if (object instanceof PostCurrency) {
            validatePostCurrency((PostCurrency) object);
        }
    }

    private void validatePostCurrency(PostCurrency postCurrency) {
        LOG.info("Validating PostCurrency: " + postCurrency);
        if(postCurrency.getCode() == null) throw new ValidationException("Code is required");
        if(postCurrency.getName() == null) throw new ValidationException("Name is required");
        if(postCurrency.getSign() == null) throw new ValidationException("Sign is required");
        if(postCurrency.getName().length() > 32) throw new ValidationException("Name is too long");
        if(postCurrency.getSign().length() > 8) throw new ValidationException("Sign is too long");
        if(!ALLOWED_CODES.contains(postCurrency.getCode())) throw new ValidationException("Code does not meet ISO-4217");
    }
}
