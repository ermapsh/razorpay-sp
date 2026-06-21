package com.ermapsh.razorpay.merchant.dto.request;

import com.ermapsh.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest(

        @NotBlank(message="Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotNull(message="Business type is required")
        BusinessType businessType,

        @NotBlank(message="Business name is required")
        @Size(min = 2, max = 50, message = "Business name must be between 2 and 50 characters")
        String businessName,

        @Email(message = "Email should be valid")
        @NotBlank(message="Email is required")
        String email,

        @NotNull(message="Password is required")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password
) {}
