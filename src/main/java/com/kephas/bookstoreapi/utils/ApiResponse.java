package com.kephas.bookstoreapi.utils;

public record ApiResponse<T>(
        int statusCode,
        boolean success,
        String message,
        T data
) {
}
