package com.ermapsh.razorpay.merchant.controller;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;
import com.ermapsh.razorpay.merchant.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<ApiResponse<MerchantSignupResponse>> signup(@RequestBody @Valid MerchantSignupRequest request) {
        try {
            MerchantSignupResponse response = authService.signup(request);
            return ResponseEntity.status(201).body(
                    new ApiResponse<>(201, "Merchant created successfully", response, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(400,
                            "Failed to create merchant",
                            null,
                            e)
            );
        }
    }

}
