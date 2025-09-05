package com.kephas.bookstoreapi.utils;


import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int statusCode ,
        boolean success,
        String message,
        T data
) {

    public static<T> ApiResponse<T> success(String message, T data){
        return new ApiResponse<>(200, true, message, data);
    }

    public static <T> ApiResponse<T> success (int code, String message, T data){
        return new ApiResponse<>(code, true, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message){
        return new ApiResponse<>(code, false, message, null);
    }

    public static <T> ApiResponse<T> error(int code, String message, T data){
        return new ApiResponse<>(code, false, message, data);
    }



}
