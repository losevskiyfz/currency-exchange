package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.service.ExchangeRateService;
import com.github.losevskiyfz.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {ExchangeRateServlet.EXCHANGE_RATES_URI_PATTERN})
public class ExchangeRateServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ExchangeRateServlet.class.getName());
    public static final String EXCHANGE_RATES_URI = "/exchangeRates";
    public static final String EXCHANGE_RATES_URI_PATTERN = EXCHANGE_RATES_URI;

    private final String currencyContentType = PropertiesProvider.get("currency.api.controller.content-type");
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals(EXCHANGE_RATES_URI)) {
            LOG.info(String.format("GET request to %s", EXCHANGE_RATES_URI));
            WebUtils.writeResponse(resp, exchangeRateService.getAll(), HttpServletResponse.SC_OK, currencyContentType);
        }
    }
}
