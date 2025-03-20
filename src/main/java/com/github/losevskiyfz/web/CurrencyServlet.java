package com.github.losevskiyfz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.NotFoundResponse;
import com.github.losevskiyfz.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.WebUtils.readRequestBody;

@WebServlet(urlPatterns = {"/currencies/*", "/currency/*"})
public class CurrencyServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CurrencyServlet.class.getName());
    private static final String CURRENCIES_URI = "/currencies";
    private static final String CURRENCY_URI = "/currency";

    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);
    private final ObjectMapper objectMapper = context.resolve(ObjectMapper.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestURI = req.getRequestURI();
            if (CURRENCIES_URI.equals(requestURI)) {
                handleGetAllCurrencies(resp);
            } else if (requestURI.startsWith(CURRENCY_URI)) {
                handleGetCurrencyByCode(req, resp);
            }
        } catch (RuntimeException e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (CURRENCIES_URI.equals(req.getRequestURI())) {
                handleCreateCurrency(req, resp);
            }
        } catch (RuntimeException e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetAllCurrencies(HttpServletResponse resp) throws IOException {
        writeResponse(resp, currencyService.getAllCurrencies(), HttpServletResponse.SC_OK);
    }

    private void handleGetCurrencyByCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String code = pathInfo.substring(1).toUpperCase();
            Optional<CurrencyDto> currencyDtoOpt = currencyService.getCurrencyByCode(code);
            if (currencyDtoOpt.isPresent()) {
                writeResponse(resp, currencyDtoOpt.get(), HttpServletResponse.SC_OK);
            } else {
                writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    private void handleCreateCurrency(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonBody = readRequestBody(req);
        CurrencyDto currencyDto = null;
        try {
            currencyDto = objectMapper.readValue(jsonBody, CurrencyDto.class);
        } catch (Exception e) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (currencyService.getCurrencyByCode(currencyDto.getCode()).isPresent()) {
            writeResponse(resp, new NotFoundResponse(), HttpServletResponse.SC_CONFLICT);
            return;
        }
        writeResponse(resp, currencyService.saveCurrency(currencyDto), HttpServletResponse.SC_CREATED);
    }


    private void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }
}

