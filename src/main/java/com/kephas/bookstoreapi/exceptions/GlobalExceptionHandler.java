package com.kephas.bookstoreapi.exceptions;

import com.kephas.bookstoreapi.utils.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Object> handleNotFound(Exception ex){
        return ApiResponse.error(404, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleGeneralException(Exception ex){
        return ApiResponse.error(500, "Something went wrong: " + ex.getMessage());
    }


}
