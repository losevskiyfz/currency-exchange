package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(urlPatterns = "/currencies/*")
public class CurrencyServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(CurrencyServlet.class);
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
