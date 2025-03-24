package com.github.losevskiyfz.config;

import com.github.losevskiyfz.controller.ExchangeRateServlet;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebFilter(urlPatterns = "/*")
public class CorsFilter implements Filter {
    private static final Logger logger = Logger.getLogger(CorsFilter.class.getName());
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        res.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization," +
                "Content-Length, Host, User-Agent, Accept, Accept-Encoding, Connection");
        res.setHeader("Access-Control-Max-Age", "1209600");;

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(request, response);
        logger.info("CORS filter executed");
    }
}