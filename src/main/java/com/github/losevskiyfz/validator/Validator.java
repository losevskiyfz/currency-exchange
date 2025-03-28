package com.github.losevskiyfz.validator;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.config.Properties;
import com.github.losevskiyfz.dto.ExchangeRequest;
import com.github.losevskiyfz.dto.PatchExchangeRate;
import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.dto.PostExchangeRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Validator {
    private static final Logger logger = Logger.getLogger(Validator.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final List<String> currencyCodes =
            Files.readAllLines(
                    Paths.get(
                            Objects.requireNonNull(
                                    Thread.currentThread().getContextClassLoader().getResource(
                                            context.resolve(Properties.class)
                                                    .getProperty("validation.currency.code.file")
                                    ))
                                    .toURI()
                    )
            );

    public Validator() throws IOException, URISyntaxException {
    }

    public void validate(Object object) {
        logger.info("Validating object " + object);
        if (object instanceof PostCurrency) {
            validatePostCurrency((PostCurrency) object);
        } else if (object instanceof PostExchangeRate) {
            validatePostExchangeRate((PostExchangeRate) object);
        } else if (object instanceof PatchExchangeRate) {
            validatePatchExchangeRate((PatchExchangeRate) object);
        } else if (object instanceof ExchangeRequest) {
            validateExchangeRequest((ExchangeRequest) object);
        }
        logger.info("Validated object " + object + " successfully");
    }

    void validatePostCurrency(PostCurrency postCurrency) {
        if (postCurrency.getCode() == null ||
                postCurrency.getName() == null ||
                postCurrency.getSign() == null ||
                postCurrency.getName().length() > 32 ||
                postCurrency.getSign().length() > 8 ||
                !currencyCodes.contains(postCurrency.getCode())
        ) {
            logger.info("Invalid posted currency " + postCurrency);
            throw new RuntimeException("Invalid posted currency " + postCurrency);
        }
    }

    void validatePostExchangeRate(PostExchangeRate postExchangeRate) {
        if (
                postExchangeRate.getRate() == null ||
                        new BigDecimal(postExchangeRate.getRate()).compareTo(BigDecimal.ZERO) <= 0 ||
                        postExchangeRate.getBaseCurrencyCode() == null ||
                        !currencyCodes.contains(postExchangeRate.getBaseCurrencyCode()) ||
                        postExchangeRate.getTargetCurrencyCode() == null ||
                        !currencyCodes.contains(postExchangeRate.getTargetCurrencyCode()) ||
                        postExchangeRate.getRate().length() > 32 ||
                        (postExchangeRate.getRate().contains("e") && postExchangeRate.getRate().length()>3) ||
                        postExchangeRate.getBaseCurrencyCode().equals(postExchangeRate.getTargetCurrencyCode())
        ) {
            logger.info("Invalid posted exchange rate " + postExchangeRate);
            throw new RuntimeException();
        }
    }

    void validatePatchExchangeRate(PatchExchangeRate exchangeRateDto) {
        if (
                exchangeRateDto.getRate() == null ||
                        new BigDecimal(exchangeRateDto.getRate()).compareTo(BigDecimal.ZERO) <= 0 ||
                exchangeRateDto.getRate().length() > 32 ||
                (exchangeRateDto.getRate().contains("e") && exchangeRateDto.getRate().length()>3)
        ) {
            logger.info("Invalid exchange rate to patch " + exchangeRateDto);
            throw new RuntimeException();
        }
    }

    void validateExchangeRequest(ExchangeRequest exchangeRequest){
        if(
                exchangeRequest.getAmount() == null ||
                        new BigDecimal(exchangeRequest.getAmount()).compareTo(BigDecimal.ZERO) <= 0 ||
                        exchangeRequest.getBaseCurrencyCode() == null ||
                        exchangeRequest.getBaseCurrencyCode().length() != 3 ||
                        exchangeRequest.getTargetCurrencyCode() == null ||
                        exchangeRequest.getTargetCurrencyCode().length() != 3 ||
                        exchangeRequest.getBaseCurrencyCode().equals(exchangeRequest.getTargetCurrencyCode())
        ) {
            logger.info("Invalid exchange request " + exchangeRequest);
            throw new RuntimeException();
        }
    }
}
