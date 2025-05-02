package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.dto.ExchangeRequest;
import com.github.losevskiyfz.service.ExchangeService;
import com.github.losevskiyfz.utils.WebUtils;
import com.github.losevskiyfz.validation.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

import static com.github.losevskiyfz.controller.CurrencyServlet.ROOT_URI;
import static com.github.losevskiyfz.utils.CurrencyUtils.round;

@WebServlet(urlPatterns = ExchangeServlet.EXCHANGE_URI_PATTERN)
public class ExchangeServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ExchangeServlet.class.getName());
    public static final String EXCHANGE_URI = "/exchange";
    public static final String EXCHANGE_URI_PATTERN = EXCHANGE_URI;
    private static final int ROUNDING_SCALE = 2;

    private final ApplicationContext context = ApplicationContext.getInstance();
    private final String currencyContentType = PropertiesProvider.get("currency.api.controller.content-type");
    private final ExchangeService exchangeService = context.resolve(ExchangeService.class);
    private final Validator validator = context.resolve(Validator.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals(ROOT_URI + EXCHANGE_URI)) {
            LOG.info(String.format("GET request to %s", EXCHANGE_URI_PATTERN));
            String sourceCode = req.getParameter("from");
            String targetCode = req.getParameter("to");
            String amount = req.getParameter("amount");
            ExchangeRequest exchangeRequest = ExchangeRequest.builder()
                    .baseCurrencyCode(sourceCode)
                    .targetCurrencyCode(targetCode)
                    .amount(amount)
                    .build();
            validator.validate(exchangeRequest);
            ExchangeDto res = exchangeService.exchange(
                    exchangeRequest.getBaseCurrencyCode(),
                    exchangeRequest.getTargetCurrencyCode(),
                    exchangeRequest.getAmount()
            );
            WebUtils.writeResponse(
                    resp,
                    roundRate(res, ROUNDING_SCALE),
                    HttpServletResponse.SC_OK,
                    currencyContentType
            );
        }
    }

    private ExchangeDto roundRate(ExchangeDto exchangeDto, int scale) {
        exchangeDto.setRate(round(exchangeDto.getRate(), scale));
        exchangeDto.setAmount(round(exchangeDto.getAmount(), scale));
        exchangeDto.setConvertedAmount(round(exchangeDto.getConvertedAmount(), scale));
        return exchangeDto;
    }
}
