package com.kephas.bookstoreapi.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kephas.bookstoreapi.utils.ApiResponse;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value for parameter '%s': expected type %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        ApiResponse<Object> response = ApiResponse.error(400, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request";

        if (ex.getCause() instanceof InvalidFormatException cause) {
            String fieldName = cause.getPath().isEmpty() ? "unknown field" : cause.getPath().get(0).getFieldName();
            String targetType = cause.getTargetType() != null ? cause.getTargetType().getSimpleName() : "unknown type";

            message = String.format("Invalid value for field '%s': expected a valid %s", fieldName, targetType);
        }
        ApiResponse<Object> response = ApiResponse.error(400, message);
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


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {

        ApiResponse<Object> response = ApiResponse.error(405, "Request method '" + ex.getMethod() + "' is not supported on this endpoint", null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(UniqueConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleUniqueConstraintViolationException(UniqueConstraintViolationException ex) {
        ApiResponse<Object> response = ApiResponse.error(409, "Conflict: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse<Object> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentialsException(InvalidLoginCredentialsException ex) {
        ApiResponse<Object> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(Exception.class)
    public  ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex){
        ApiResponse<Object> response = ApiResponse.error(500, "Internal Server Error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}
