package com.ermapsh.razorpay.merchant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppUserLogInRequest(
        @Email(message = "Email should be valid")
        @NotBlank(message="Email is required")
        String email,

        @NotNull(message="Password is required")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password
) {
}
