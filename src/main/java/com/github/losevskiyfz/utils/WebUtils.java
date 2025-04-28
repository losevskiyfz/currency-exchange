package com.github.losevskiyfz.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeResponse(HttpServletResponse resp, Object responseObj, int statusCode, String contentType) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType(contentType);
        objectMapper.writeValue(resp.getWriter(), responseObj);
    }
}
