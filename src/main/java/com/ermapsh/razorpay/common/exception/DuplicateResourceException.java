package com.ermapsh.razorpay.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final Integer errorCode = HttpStatus.CONFLICT.value();

    public  DuplicateResourceException(String message) {
        super(message);
    }
}
