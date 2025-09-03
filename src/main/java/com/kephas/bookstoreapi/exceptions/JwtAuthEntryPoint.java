package com.kephas.bookstoreapi.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kephas.bookstoreapi.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String message = "Authentication Failed";

        if (authException.getCause() != null) {
            message += ": " + authException.getCause().getMessage();
        } else {
            message += ": " + authException.getMessage();
        }

        ApiResponse<Object> apiResponse = ApiResponse.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
