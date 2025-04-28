package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/currencies/*")
public class CurrencyServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(CurrencyServlet.class.getName());
    private static final String CURRENCIES_URI = "/currencies";

    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (CURRENCIES_URI.equals(req.getRequestURI())) {
            LOG.info("GET request to /currencies");
            WebUtils.writeResponse(resp, currencyService.getAll(), HttpServletResponse.SC_OK, "application/json");
        }
    }
}
