package com.github.losevskiyfz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.NotFoundResponse;
import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.dto.validator.Validator;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import com.github.losevskiyfz.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/currencies/*", "/currency/*"})
public class CurrencyServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CurrencyServlet.class.getName());
    private static final String CURRENCIES_URI = "/currencies";
    private static final String CURRENCY_URI = "/currency";
    private static final int SLASH_PLUS_CURRENCY_CODE_SIZE = 4;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);
    private final ObjectMapper objectMapper = context.resolve(ObjectMapper.class);
    private final Validator validator = context.resolve(Validator.class);
    private final CurrencyMapper mapper = CurrencyMapper.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Processing request: " + req.getRequestURI() + " " + req.getMethod());
            String requestURI = req.getRequestURI();
            if (CURRENCIES_URI.equals(requestURI)) {
                handleGetAllCurrencies(resp);
            } else if (requestURI.startsWith(CURRENCY_URI)) {
                handleGetCurrencyByCode(req, resp);
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
            if (CURRENCIES_URI.equals(req.getRequestURI())) {
                handlePostCurrency(req, resp);
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetAllCurrencies(HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        writeResponse(resp, currencyService.getAll(), HttpServletResponse.SC_OK);
    }

    private void handleGetCurrencyByCode(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() != SLASH_PLUS_CURRENCY_CODE_SIZE) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String code = pathInfo.substring(1).toUpperCase();
        Optional<CurrencyDto> currencyDtoOpt = currencyService.getByCode(code);
        if (currencyDtoOpt.isPresent()) {
            writeResponse(resp, currencyDtoOpt.get(), HttpServletResponse.SC_OK);
        } else {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handlePostCurrency(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, InterruptedException {
        PostCurrency postCurrency = null;
        try {
            postCurrency = PostCurrency.builder()
                    .name(req.getParameter("name"))
                    .code(req.getParameter("code"))
                    .sign(req.getParameter("sign"))
                    .build();
            validator.validate(postCurrency);
        } catch (Exception e) {
            logger.info(e.getMessage());
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        }
        if (currencyService.getByCode(postCurrency.getCode()).isPresent()) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_CONFLICT);
            return;
        }
        writeResponse(resp, currencyService.create(mapper.toCurrencyDto(postCurrency)), HttpServletResponse.SC_CREATED);
    }


    private void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }
}
