package com.ermapsh.razorpay.vault.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {}
)
public @interface ExpiryYear {

    String message() default "Expiry Year cannot be in past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
