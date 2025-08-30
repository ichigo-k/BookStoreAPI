package com.kephas.bookstoreapi.exceptions;

import com.kephas.bookstoreapi.utils.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public  ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public  ResponseEntity<ApiResponse<Object>> handleMissingRoute(NoResourceFoundException ex){
        String errorMessage = String.format("Endpoint '%s' not found", ex.getResourcePath());
        ApiResponse<Object> response = ApiResponse.error(404, errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ApiResponse<Map<String,String>> response = ApiResponse.error(400, "Invalid Request Body", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public  ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintException(ConstraintViolationException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String,String>> response = ApiResponse.error(400, "Invalid Request Body", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public  ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex){
        ApiResponse<Object> response = ApiResponse.error(500, "Something went wrong: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}
