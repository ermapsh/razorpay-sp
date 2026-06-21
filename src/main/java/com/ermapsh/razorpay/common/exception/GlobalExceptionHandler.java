package com.ermapsh.razorpay.common.exception;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getErrorCode(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFound ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getErrorCode(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null,
                errors
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleEmptyBody(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Request body is required",
                        null,
                        null
                )
        );
    }

}