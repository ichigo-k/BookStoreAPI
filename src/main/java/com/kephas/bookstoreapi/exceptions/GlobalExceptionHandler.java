package com.kephas.bookstoreapi.exceptions;

import com.kephas.bookstoreapi.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public  ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public  ResponseEntity<ApiResponse<Object>> handleMissingRoute(NoHandlerFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public  ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex){
        ApiResponse<Object> response = ApiResponse.error(500, "Something went wrong: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}
