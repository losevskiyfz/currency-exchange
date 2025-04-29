package com.github.losevskiyfz.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.exception.InvalidPathInfoException;
import com.github.losevskiyfz.exception.PathInfoNotDefinedException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.github.losevskiyfz.conf.AllowedCodesProvider.getAllowedCodes;

public class WebUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<String> ALLOWED_CODES = getAllowedCodes();

    public static void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode, String contentType) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType(contentType);
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }

    public static void writeResponse(HttpServletResponse resp, int statusCode) throws IOException {
        resp.setStatus(statusCode);
    }

    public static String validateAndExtractPathInfo(String pathInfo, int expectedLength) {
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            throw new PathInfoNotDefinedException("Code is not defined");
        }
        String code = pathInfo.substring(1).toUpperCase();
        if (pathInfo.length() != expectedLength) {
            throw new InvalidPathInfoException(String.format("Invalid code: %s. Code length should be %d, but is %d", code, expectedLength - 1, code.length()));
        }
        if (!ALLOWED_CODES.contains(code)) {
            throw new InvalidPathInfoException(String.format("Code: %s. Code does not meet ISO-4217", code));
        }
        return code;
    }
}
