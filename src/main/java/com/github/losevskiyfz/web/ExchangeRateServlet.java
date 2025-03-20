package com.github.losevskiyfz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.NotFoundResponseDto;
import com.github.losevskiyfz.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/exchangeRates/*", "/exchangeRate/*"})
public class ExchangeRateServlet extends HttpServlet {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ObjectMapper objectMapper = context.resolve(ObjectMapper.class);
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals("/exchangeRates")) {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.getAllExchangeRates());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPatch(req, resp);
    }
}
