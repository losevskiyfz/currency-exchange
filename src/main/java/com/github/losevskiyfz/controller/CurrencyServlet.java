package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {CurrencyServlet.CURRENCIES_URI_PATTERN, CurrencyServlet.CURRENCY_URI_PATTERN})
public class CurrencyServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(CurrencyServlet.class.getName());
    public static final String CURRENCIES_URI = "/currencies";
    public static final String CURRENCIES_URI_PATTERN = CURRENCIES_URI;
    public static final String CURRENCY_URI = "/currency";
    public static final String CURRENCY_URI_PATTERN = CURRENCY_URI + "/*";
    public static final int SLASH_PLUS_CURRENCY_CODE_SIZE = 4;

    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);
    private final String currencyContentType = PropertiesProvider.get("currency.api.controller.content-type");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals(CURRENCIES_URI)) {
            LOG.info(String.format("GET request to %s", CURRENCIES_URI));
            WebUtils.writeResponse(resp, currencyService.getAll(), HttpServletResponse.SC_OK, currencyContentType);
        } else if (req.getRequestURI().startsWith(CURRENCY_URI)) {
            LOG.info(String.format("GET request to %s", CURRENCY_URI));
            String code = WebUtils.validateAndExtractPathInfo(req.getPathInfo(), SLASH_PLUS_CURRENCY_CODE_SIZE);
            WebUtils.writeResponse(resp, currencyService.getByCode(code), HttpServletResponse.SC_OK, currencyContentType);
        }
        super.doGet(req, resp);
    }
}
