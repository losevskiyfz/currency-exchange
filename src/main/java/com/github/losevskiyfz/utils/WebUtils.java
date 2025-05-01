package com.github.losevskiyfz.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.exception.InvalidPathInfoException;
import com.github.losevskiyfz.exception.PathInfoNotDefinedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, String> parseFormUrlEncoded(String body) {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    public static String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString();
    }
}
