package com.github.losevskiyfz.controller;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.PatchExchangeRate;
import com.github.losevskiyfz.dto.PostExchangeRate;
import com.github.losevskiyfz.service.ExchangeRateService;
import com.github.losevskiyfz.utils.WebUtils;
import com.github.losevskiyfz.validation.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.losevskiyfz.controller.CurrencyServlet.ROOT_URI;
import static com.github.losevskiyfz.utils.CurrencyUtils.round;
import static com.github.losevskiyfz.utils.WebUtils.readRequestBody;

@WebServlet(urlPatterns = {ExchangeRateServlet.EXCHANGE_RATES_URI_PATTERN, ExchangeRateServlet.EXCHANGE_RATE_URI_PATTERN})
public class ExchangeRateServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ExchangeRateServlet.class.getName());
    public static final String EXCHANGE_RATES_URI = "/exchangeRates";
    public static final String EXCHANGE_RATES_URI_PATTERN = EXCHANGE_RATES_URI;
    public static final String EXCHANGE_RATE_URI = "/exchangeRate";
    public static final String EXCHANGE_RATE_URI_PATTERN = EXCHANGE_RATE_URI + "/*";
    public static final int SLASH_PLUS_TWO_CURRENCY_CODES_SIZE = 7;
    public static final int CODE_SIZE = 3;
    private static final int ROUNDING_SCALE = 2;

    private final String currencyContentType = PropertiesProvider.get("currency.api.controller.content-type");
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateService exchangeRateService = context.resolve(ExchangeRateService.class);
    private final Validator validator = context.resolve(Validator.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info(req.getRequestURI());
        if (req.getRequestURI().equals(ROOT_URI + EXCHANGE_RATES_URI)) {
            LOG.info(String.format("GET request to %s", EXCHANGE_RATES_URI_PATTERN));
            List<ExchangeRateDto> res = exchangeRateService.getAll().stream()
                    .map(er -> roundRate(er, ROUNDING_SCALE))
                    .collect(Collectors.toList());
            WebUtils.writeResponse(resp, res, HttpServletResponse.SC_OK, currencyContentType);
        } else if (req.getRequestURI().startsWith(ROOT_URI + EXCHANGE_RATE_URI)) {
            LOG.info(String.format("GET request to %s", EXCHANGE_RATE_URI_PATTERN));
            String codePair = WebUtils.validateAndExtractPathInfo(
                    req.getPathInfo(),
                    SLASH_PLUS_TWO_CURRENCY_CODES_SIZE
            ).toUpperCase();
            String sourceCode = codePair.substring(0, CODE_SIZE);
            String targetCode = codePair.substring(CODE_SIZE);
            validator.validate(sourceCode);
            validator.validate(targetCode);
            ExchangeRateDto res = exchangeRateService.getExchangeRateBySourceAndTargetCode(sourceCode, targetCode);
            WebUtils.writeResponse(
                    resp,
                    roundRate(res, ROUNDING_SCALE),
                    HttpServletResponse.SC_OK,
                    currencyContentType
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ((ROOT_URI + EXCHANGE_RATES_URI).equals(req.getRequestURI())) {
            LOG.info(String.format("POST request to %s", EXCHANGE_RATES_URI));
            PostExchangeRate postExchangeRate = PostExchangeRate.builder()
                    .baseCurrencyCode(req.getParameter("baseCurrencyCode"))
                    .targetCurrencyCode(req.getParameter("targetCurrencyCode"))
                    .rate(req.getParameter("rate"))
                    .build();
            validator.validate(postExchangeRate);
            ExchangeRateDto res = exchangeRateService.save(postExchangeRate);
            WebUtils.writeResponse(
                    resp,
                    res,
                    HttpServletResponse.SC_OK,
                    currencyContentType
            );
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().startsWith(ROOT_URI + EXCHANGE_RATE_URI)) {
            LOG.info(String.format("PATCH request to %s", EXCHANGE_RATE_URI_PATTERN));
            String codePair = WebUtils.validateAndExtractPathInfo(
                    req.getPathInfo(),
                    SLASH_PLUS_TWO_CURRENCY_CODES_SIZE
            ).toUpperCase();
            String baseCode = codePair.substring(0, CODE_SIZE);
            String targetCode = codePair.substring(CODE_SIZE);
            validator.validate(baseCode);
            validator.validate(targetCode);

            String requestBody = readRequestBody(req);
            Map<String,String> params = WebUtils.parseFormUrlEncoded(requestBody);
            PatchExchangeRate patchExchangeRate = PatchExchangeRate.builder().rate(params.get("rate")).build();
            validator.validate(patchExchangeRate);

            ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
                    .baseCurrency(CurrencyDto.builder().code(baseCode).build())
                    .targetCurrency(CurrencyDto.builder().code(targetCode).build())
                    .rate(new BigDecimal(patchExchangeRate.getRate()))
                    .build();

            ExchangeRateDto res = exchangeRateService.update(exchangeRateDto);
            WebUtils.writeResponse(
                    resp,
                    res,
                    HttpServletResponse.SC_OK,
                    currencyContentType
            );
        }
    }

    private ExchangeRateDto roundRate(ExchangeRateDto exchangeRateDto, int scale) {
        exchangeRateDto.setRate(round(exchangeRateDto.getRate(), scale));
        return exchangeRateDto;
    }
}
