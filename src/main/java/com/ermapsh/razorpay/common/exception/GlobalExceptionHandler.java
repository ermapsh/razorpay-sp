package com.ermapsh.razorpay.common.exception;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getErrorCode(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
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

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessRuleViolation(
            BusinessRuleViolationException ex) {

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null,
                        null
                )
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingRequestHeader(
            MissingRequestHeaderException ex) {

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getHeaderName() + " header is required",
                        null,
                        null
                )
        );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null,
                        null
                )
        );
    }

    @ExceptionHandler(RateLimiterException.class)
    public ResponseEntity<ApiResponse<?>> handleRateLimiterException(
            RateLimiterException ex) {

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-RateLimit-Remaining", String.valueOf(0))
                .header("Retry-after", String.valueOf(ex.getRetryAfterSeconds()))
                .header("X-Rate-Reset", String.valueOf(Instant.now().plusSeconds(ex.getRetryAfterSeconds()).getEpochSecond()))
                .body(new ApiResponse<>(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        ex.getMessage(),
                        null,
                        "RATE_LIMIT_EXCEEDED"
                )
        );
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<Map<String, Object>> handleIdempotencyConflict(
            IdempotencyConflictException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "IDEMPOTENCY_CONFLICT",
                        "message", ex.getMessage()
                ));
    }
}