package com.ermapsh.razorpay.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessRuleViolationException extends RuntimeException{
    private final Integer errorCode;

    public BusinessRuleViolationException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
