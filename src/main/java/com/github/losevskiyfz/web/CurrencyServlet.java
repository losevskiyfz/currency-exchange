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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/currencies/*", "/currency/*"})
public class CurrencyServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CurrencyServlet.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);
    private final ObjectMapper objectMapper = context.resolve(ObjectMapper.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().equals("/currencies")) {
            objectMapper.writeValue(resp.getWriter(), currencyService.getAllCurrencies());
        } else if (req.getRequestURI().startsWith("/currency")) {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null) {
                String code = pathInfo.substring(1).toUpperCase();
                Optional<CurrencyDto> currencyDtoOpt = currencyService.getCurrencyByCode(code);
                if (currencyDtoOpt.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), currencyDtoOpt.get());
                } else {
                    objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
                }
            } else {
                objectMapper.writeValue(resp.getWriter(), new NotFoundResponse());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().equals("/currencies")) {
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonBody = sb.toString();
            CurrencyDto currencyDto = objectMapper.readValue(jsonBody, CurrencyDto.class);
            currencyService.saveCurrency(currencyDto);
        }
    }
}
