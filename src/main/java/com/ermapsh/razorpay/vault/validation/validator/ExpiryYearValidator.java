package com.ermapsh.razorpay.vault.validation.validator;

import com.ermapsh.razorpay.vault.validation.ExpiryYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ExpiryYearValidator
        implements ConstraintValidator<ExpiryYear, Integer> {

    @Override
    public boolean isValid(
            Integer inputYear,
            ConstraintValidatorContext context
    ) {
        if (inputYear == null) {
            return true;
        }

        int currentYear = Year.now().getValue();

        return inputYear >= currentYear;
    }
}