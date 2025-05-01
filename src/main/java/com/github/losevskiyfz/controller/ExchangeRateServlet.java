package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.dto.PostExchangeRate;
import com.github.losevskiyfz.service.ExchangeRateService;
import com.github.losevskiyfz.utils.WebUtils;
import com.github.losevskiyfz.validation.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {ExchangeRateServlet.EXCHANGE_RATES_URI_PATTERN, ExchangeRateServlet.EXCHANGE_RATE_URI_PATTERN})
public class ExchangeRateServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ExchangeRateServlet.class.getName());
    public static final String EXCHANGE_RATES_URI = "/exchangeRates";
    public static final String EXCHANGE_RATES_URI_PATTERN = EXCHANGE_RATES_URI;
    public static final String EXCHANGE_RATE_URI = "/exchangeRate";
    public static final String EXCHANGE_RATE_URI_PATTERN = EXCHANGE_RATE_URI + "/*";
    public static final int SLASH_PLUS_TWO_CURRENCY_CODES_SIZE = 7;
    public static final int CODE_SIZE = 3;

    private final String currencyContentType = PropertiesProvider.get("currency.api.controller.content-type");
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);
    private final Validator validator = context.resolve(Validator.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info(req.getRequestURI());
        if (req.getRequestURI().equals(EXCHANGE_RATES_URI)) {
            LOG.info(String.format("GET request to %s", EXCHANGE_RATES_URI_PATTERN));
            WebUtils.writeResponse(resp, exchangeRateService.getAll(), HttpServletResponse.SC_OK, currencyContentType);
        } else if (req.getRequestURI().startsWith(EXCHANGE_RATE_URI)) {
            LOG.info(String.format("GET request to %s", EXCHANGE_RATE_URI_PATTERN));
            String codePair = WebUtils.validateAndExtractPathInfo(
                    req.getPathInfo(),
                    SLASH_PLUS_TWO_CURRENCY_CODES_SIZE
            ).toUpperCase();
            String sourceCode = codePair.substring(0, CODE_SIZE);
            String targetCode = codePair.substring(CODE_SIZE);
            validator.validate(sourceCode);
            validator.validate(targetCode);
            WebUtils.writeResponse(
                    resp,
                    exchangeRateService.getExchangeRateBySourceAndTargetCode(sourceCode, targetCode),
                    HttpServletResponse.SC_OK,
                    currencyContentType
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (EXCHANGE_RATES_URI.equals(req.getRequestURI())) {
            LOG.info(String.format("POST request to %s", EXCHANGE_RATES_URI));
            PostExchangeRate postExchangeRate = PostExchangeRate.builder()
                    .baseCurrencyCode(req.getParameter("baseCurrencyCode"))
                    .targetCurrencyCode(req.getParameter("targetCurrencyCode"))
                    .rate(req.getParameter("rate"))
                    .build();
            validator.validate(postExchangeRate);
            WebUtils.writeResponse(
                    resp,
                    exchangeRateService.save(postExchangeRate),
                    HttpServletResponse.SC_OK,
                    currencyContentType
            );
        }
    }
}
