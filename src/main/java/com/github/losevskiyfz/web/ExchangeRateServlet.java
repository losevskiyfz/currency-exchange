package com.github.losevskiyfz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.NotFoundResponse;
import com.github.losevskiyfz.dto.RateRequest;
import com.github.losevskiyfz.service.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/exchangeRates/*", "/exchangeRate/*", "/exchange"})
public class ExchangeRateServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ExchangeRateServlet.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ObjectMapper objectMapper = context.resolve(ObjectMapper.class);
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);
    private static final int SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE = 7;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().equals("/exchangeRates")) {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.getAllExchangeRates());
        } else if (req.getRequestURI().startsWith("/exchangeRate")) {
            String pathInfo = req.getPathInfo();
            if (pathInfo.length() == SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE) {
                String baseCode = pathInfo.substring(1, 4);
                String targetCode = pathInfo.substring(4, 7);
                Optional<ExchangeRateDto> exchangeRateDtoOpt = exchangeRateService.getExchangeRates(baseCode, targetCode);
                if (exchangeRateDtoOpt.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), exchangeRateDtoOpt.get());
                } else {
                    objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
                }
            } else {
                objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
            }
        } else if (req.getRequestURI().startsWith("/exchange")) {
            String fromCurrency = req.getParameter("from");
            String toCurrency = req.getParameter("to");
            String amountStr = req.getParameter("amount");
            Optional<ExchangeDto> exchangeDto = exchangeRateService.getExchange(fromCurrency, toCurrency, amountStr);
            if (exchangeDto.isPresent()) {
                objectMapper.writeValue(resp.getWriter(), exchangeDto.get());
            } else {
                objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().equals("/exchangeRates")) {
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonBody = sb.toString();
            ExchangeRateDto exchangeRateDto = objectMapper.readValue(jsonBody, ExchangeRateDto.class);
            exchangeRateService.saveExchangeRate(exchangeRateDto);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().startsWith("/exchangeRate")) {
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonBody = sb.toString();
            RateRequest rateRequest = objectMapper.readValue(jsonBody, RateRequest.class);
            String pathInfo = req.getPathInfo();
            if (pathInfo.length() == SLASH_PLUS_CURRENCY_CODES_PAIR_SIZE) {
                String baseCode = pathInfo.substring(1, 4);
                String targetCode = pathInfo.substring(4, 7);
                Optional<ExchangeRateDto> exchangeRateDtoOpt = exchangeRateService.getExchangeRates(baseCode, targetCode);
                if (exchangeRateDtoOpt.isPresent()) {
                    ExchangeRateDto exchangeRateDto = exchangeRateDtoOpt.get();
                    exchangeRateDto.setRate(rateRequest.getRate());
                    exchangeRateService.saveExchangeRate(exchangeRateDto);
                } else {
                    objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
                }
            } else {
                objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
            }
        }
    }
}
