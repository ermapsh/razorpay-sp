package com.ermapsh.razorpay.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)

public record ApiResponse<T>(
        Integer code,
        String message,
        T data,
        Object error,
        LocalDateTime timestamp
) {

    public ApiResponse(Integer code, String message, T data, Object error) {
        this(code, message, data, error, LocalDateTime.now());
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return build(HttpStatus.OK, message, data, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return build(HttpStatus.CREATED, message, data, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> accepted(String message, T data) {
        return build(HttpStatus.ACCEPTED, message, data, null);
    }

    public static ResponseEntity<ApiResponse<Void>> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<ApiResponse<Void>> error(
            HttpStatus status,
            String message,
            Object error) {
        return build(status, message, null, error);
    }

    public static <T> ResponseEntity<ApiResponse<T>> of(
            HttpStatus status,
            String message,
            T data) {
        return build(status, message, data, null);
    }

    private static <T> ResponseEntity<ApiResponse<T>> build(
            HttpStatus status,
            String message,
            T data,
            Object error) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(
                        status.value(),
                        message,
                        data,
                        error
                ));
    }

}