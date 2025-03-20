package com.github.losevskiyfz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.*;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.service.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.WebUtils.readRequestBody;

@WebServlet(urlPatterns = {"/exchangeRates/*", "/exchangeRate/*", "/exchange"})
public class ExchangeRateServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ExchangeRateServlet.class.getName());
    private static final int SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE = 7;
    private static final String EXCHANGE_RATES_URI = "/exchangeRates";
    private static final String EXCHANGE_RATE_URI = "/exchangeRate";
    private static final String EXCHANGE_URI = "/exchange";

    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ObjectMapper objectMapper = context.resolve(ObjectMapper.class);
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestURI = req.getRequestURI();
            if (EXCHANGE_RATES_URI.equals(requestURI)) {
                handleGetAllExchangeRates(resp);
            } else if (requestURI.startsWith(EXCHANGE_RATE_URI)) {
                handleGetExchangeRate(req, resp);
            } else if (requestURI.startsWith(EXCHANGE_URI)) {
                handleExchangeRateConversion(req, resp);
            }
        } catch (RuntimeException e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (EXCHANGE_RATES_URI.equals(req.getRequestURI())) {
                handleCreateExchangeRate(req, resp);
            }
        } catch (RuntimeException e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (req.getRequestURI().startsWith(EXCHANGE_RATE_URI)) {
                handleUpdateExchangeRate(req, resp);
            }
        } catch (RuntimeException e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetAllExchangeRates(HttpServletResponse resp) throws IOException {
        writeResponse(resp, exchangeRateService.getAllExchangeRates(), HttpServletResponse.SC_OK);
    }

    private void handleGetExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo.length() != SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String baseCode = pathInfo.substring(1, 4);
            String targetCode = pathInfo.substring(4, 7);
            Optional<ExchangeRateDto> exchangeRateDtoOpt = exchangeRateService.getExchangeRate(baseCode, targetCode);
            if (exchangeRateDtoOpt.isPresent()) {
                writeResponse(resp, exchangeRateDtoOpt.get(), HttpServletResponse.SC_OK);
            } else {
                writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
            }
        }

    }

    private void handleExchangeRateConversion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sourceCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        String amountStr = req.getParameter("amount");
        if (
                amountStr == null || amountStr.isEmpty() ||
                        targetCurrency == null || targetCurrency.isEmpty() ||
                        sourceCurrency == null || sourceCurrency.isEmpty()
        ) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        }
        Optional<ExchangeDto> exchangeDtoOpt = exchangeRateService.exchange(sourceCurrency, targetCurrency, amountStr);
        if (exchangeDtoOpt.isPresent()) {
            writeResponse(resp, exchangeDtoOpt.get(), HttpServletResponse.SC_OK);
        } else {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleCreateExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonBody = readRequestBody(req);
        ExchangeRateDto exchangeRateDto = null;
        try {
            exchangeRateDto = objectMapper.readValue(jsonBody, ExchangeRateDto.class);
        } catch (Exception e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String baseCurrencyCode = exchangeRateDto.getBaseCurrency().getCode();
        String targetCurrencyCode = exchangeRateDto.getTargetCurrency().getCode();
        Optional<ExchangeRateDto> exchangeRateDtoOpt = exchangeRateService.getExchangeRate(
                baseCurrencyCode,
                targetCurrencyCode
        );
        if (exchangeRateDtoOpt.isPresent()) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        } else {
            Optional<CurrencyDto> baseOpt = currencyService.getCurrencyByCode(baseCurrencyCode);
            Optional<CurrencyDto> targetOpt = currencyService.getCurrencyByCode(targetCurrencyCode);
            if (baseOpt.isEmpty() || targetOpt.isEmpty()) {
                writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
            } else {
                writeResponse(resp, exchangeRateService.saveExchangeRate(exchangeRateDto), HttpServletResponse.SC_OK);
            }
        }
    }

    private void handleUpdateExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonBody = readRequestBody(req);
        RateRequest rateRequest = objectMapper.readValue(jsonBody, RateRequest.class);
        String pathInfo = req.getPathInfo();
        if (pathInfo.length() != SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String baseCode = pathInfo.substring(1, 4);
            String targetCode = pathInfo.substring(4, 7);
            Optional<ExchangeRateDto> exchangeRateDtoOpt = exchangeRateService.getExchangeRate(baseCode, targetCode);
            if (exchangeRateDtoOpt.isPresent()) {
                ExchangeRateDto exchangeRateDto = exchangeRateDtoOpt.get();
                exchangeRateDto.setRate(new BigDecimal(rateRequest.getRate()));
                writeResponse(resp, exchangeRateService.updateExchangeRate(exchangeRateDto), HttpServletResponse.SC_OK);
            } else {
                writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    private void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }
}
