package com.github.losevskiyfz.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.exception.InvalidPathInfoException;
import com.github.losevskiyfz.exception.PathInfoNotDefinedException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode, String contentType) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType(contentType);
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }

    public static void writeResponse(HttpServletResponse resp, int statusCode) {
        resp.setStatus(statusCode);
    }

    public static String validateAndExtractPathInfo(String pathInfo, int expectedLength) {
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            throw new PathInfoNotDefinedException("Path info is not defined");
        }
        if (pathInfo.length() != expectedLength) {
            throw new InvalidPathInfoException(String.format("Invalid path info: %s. Path info expected to be %d, but is %d", pathInfo, expectedLength, pathInfo.length()));
        }
        return pathInfo.substring(1);
    }
}
