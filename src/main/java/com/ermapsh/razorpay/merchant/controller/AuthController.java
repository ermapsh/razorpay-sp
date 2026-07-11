package com.ermapsh.razorpay.merchant.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;
import com.ermapsh.razorpay.merchant.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<ApiResponse<MerchantSignupResponse>> signup(@RequestBody @Valid MerchantSignupRequest request) {
        MerchantSignupResponse response = authService.signup(request);
        return ApiResponse.created(
                "Merchant created successfully",
                response
        );
    }

}
