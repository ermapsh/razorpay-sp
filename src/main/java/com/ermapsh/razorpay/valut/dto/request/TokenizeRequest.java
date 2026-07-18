package com.ermapsh.razorpay.valut.dto.request;

import com.ermapsh.razorpay.valut.validation.ExpiryYear;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(

        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "Invalid card number")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "PAN length is invalid")
        String pan,

        @NotBlank(message = "cvv is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV length is invalid")
        String cvv,

        @NotNull(message = "expiry month is required")
        @Min(value = 1, message = "Expiry month must to between 1 to 12")
        @Min(value = 12, message = "Expiry month must to between 1 to 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry year cannot be null")
        @ExpiryYear
        Integer expiryYear,

        UUID customerId
) {
}
