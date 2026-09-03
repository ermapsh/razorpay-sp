package com.ermapsh.razorpay.common.exception;

import lombok.Getter;

@Getter
public class RateLimiterException extends RuntimeException {

    private final int retryAfterSeconds;
    private final int remaining = 0;

    public RateLimiterException(String message, int retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }


}