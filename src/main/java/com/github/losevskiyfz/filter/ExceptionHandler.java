package com.github.losevskiyfz.filter;

import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.dto.ErrorResponse;
import com.github.losevskiyfz.exception.InvalidPathInfoException;
import com.github.losevskiyfz.exception.PathInfoNotDefinedException;
import com.github.losevskiyfz.exception.SqlObjectNotFoundException;
import com.github.losevskiyfz.listener.ContextInitializer;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

import static com.github.losevskiyfz.utils.WebUtils.writeResponse;

@WebFilter("/*")
public class ExceptionHandler implements Filter {
    private final String currencyContentType = PropertiesProvider.get("currency.api.controller.content-type");
    private static final Logger LOG = Logger.getLogger(ExceptionHandler.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Safe cast, because @WebFilter catch only http requests
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (PathInfoNotDefinedException e) {
            LOG.info(e.getMessage());
            writeResponse(
                    httpResponse,
                    HttpServletResponse.SC_NOT_FOUND
            );
        } catch (InvalidPathInfoException e) {
            LOG.info(e.getMessage());
            writeResponse(
                    httpResponse,
                    ErrorResponse.builder().message(e.getMessage()).build(),
                    HttpServletResponse.SC_BAD_REQUEST,
                    currencyContentType
            );
        } catch (SqlObjectNotFoundException e) {
            LOG.info(e.getMessage());
            writeResponse(
                    httpResponse,
                    ErrorResponse.builder().message(e.getMessage()).build(),
                    HttpServletResponse.SC_NOT_FOUND,
                    currencyContentType
            );
        } catch (Exception e) {
            LOG.info(e.getMessage());
            writeResponse(
                    httpResponse,
                    ErrorResponse.builder().message(e.getMessage()).build(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    currencyContentType
            );
        }
    }
}