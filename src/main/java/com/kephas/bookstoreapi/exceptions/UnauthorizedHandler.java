package com.kephas.bookstoreapi.exceptions;

import com.kephas.bookstoreapi.utils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class UnauthorizedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");


        ApiResponse<Object> apiResponse = ApiResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "Access Denied: You do not have permission to access this resource"
        );
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
