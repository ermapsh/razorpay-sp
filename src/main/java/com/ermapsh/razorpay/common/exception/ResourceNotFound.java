package com.ermapsh.razorpay.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFound extends RuntimeException {
    private final Integer errorCode;

    public ResourceNotFound(String message) {
        super(message);
        this.errorCode = HttpStatus.NOT_FOUND.value();
    }
}
