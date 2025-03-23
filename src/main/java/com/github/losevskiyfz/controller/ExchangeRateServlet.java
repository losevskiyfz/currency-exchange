package com.github.losevskiyfz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.*;
import com.github.losevskiyfz.dto.validator.Validator;
import com.github.losevskiyfz.mapper.ExchangeRateMapper;
import com.github.losevskiyfz.service.ExchangeRateService;
import com.github.losevskiyfz.service.ExchangeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.CurrencyUtils.round;

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
    private final ExchangeService exchangeService = context.resolve(ExchangeService.class);
    private final Validator validator = context.resolve(Validator.class);
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Processing request: " + req.getRequestURI() + " " + req.getMethod());
            String requestURI = req.getRequestURI();
            if (EXCHANGE_RATES_URI.equals(requestURI)) {
                handleGetAllExchangeRates(resp);
            } else if (requestURI.startsWith(EXCHANGE_RATE_URI)) {
                handleGetExchangeRate(req, resp);
            } else if (requestURI.startsWith(EXCHANGE_URI)) {
                handleExchangeRateConversion(req, resp);
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Processing request: " + req.getRequestURI() + " " + req.getMethod());
            if (EXCHANGE_RATES_URI.equals(req.getRequestURI())) {
                handleCreateExchangeRate(req, resp);
            }
        } catch (ConstraintViolationException e) {
            logger.info(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_CONFLICT);
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Processing request: " + req.getRequestURI() + " " + req.getMethod());
            if (req.getRequestURI().startsWith(EXCHANGE_RATE_URI)) {
                handleUpdateExchangeRate(req, resp);
            }
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetAllExchangeRates(HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        writeResponse(resp, exchangeRateService.getAll(), HttpServletResponse.SC_OK);
    }

    private void handleGetExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        String pathInfo = req.getPathInfo();
        if (pathInfo.length() != SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String baseCode = pathInfo.substring(1, 4).toUpperCase();
            String targetCode = pathInfo.substring(4, 7).toUpperCase();
            Optional<ExchangeRateDto> exchangeRateDtoOpt = exchangeRateService.getByCodes(baseCode, targetCode);
            if (exchangeRateDtoOpt.isPresent()) {
                ExchangeRateDto exchangeRateDto = exchangeRateDtoOpt.get();
                exchangeRateDto.setRate(round(exchangeRateDto.getRate(), 2));
                writeResponse(resp, exchangeRateDto, HttpServletResponse.SC_OK);
            } else {
                writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
            }
        }

    }

    private void handleExchangeRateConversion(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        String sourceCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        String amountStr = req.getParameter("amount");
        if (
                amountStr == null || amountStr.isEmpty() ||
                        sourceCurrency == null || sourceCurrency.isEmpty() ||
                        targetCurrency == null || targetCurrency.isEmpty()
        ) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        }
        sourceCurrency = sourceCurrency.toUpperCase();
        targetCurrency = targetCurrency.toUpperCase();
        Optional<Exchange> exchangeOptional = exchangeService.exchange(sourceCurrency, targetCurrency, amountStr);
        if (exchangeOptional.isPresent()) {
            Exchange res = exchangeOptional.get();
            res.setRate(round(res.getRate(), 2));
            writeResponse(resp, exchangeOptional.get(), HttpServletResponse.SC_OK);
        } else {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleCreateExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        PostExchangeRate postExchangeRate = null;
        try {
            postExchangeRate = PostExchangeRate.builder()
                    .rate(new BigDecimal(req.getParameter("rate")))
                    .baseCurrencyCode(req.getParameter("baseCurrencyCode"))
                    .targetCurrencyCode(req.getParameter("targetCurrencyCode"))
                    .build();
            validator.validate(postExchangeRate);
        } catch (Exception e) {
            logger.info(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        writeResponse(resp, exchangeRateService.create(mapper.toExchangeRateDto(postExchangeRate)), HttpServletResponse.SC_CREATED);
    }

    private void handleUpdateExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        PatchExchangeRate patchExchangeRate = null;
        try {
            patchExchangeRate = PatchExchangeRate.builder()
                    .rate(new BigDecimal(req.getParameter("rate"))).build();
            validator.validate(patchExchangeRate);
        } catch (Exception e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() != SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String baseCode = pathInfo.substring(1, 4).toUpperCase();
            String targetCode = pathInfo.substring(4, 7).toUpperCase();
            ExchangeRateDto exchangeRateDto = mapper.toExchangeRateDto(patchExchangeRate, baseCode, targetCode);
            writeResponse(resp, exchangeRateService.update(exchangeRateDto), HttpServletResponse.SC_OK);
        }
    }

    private void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }
}
