package com.ermapsh.razorpay.merchant.controller;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import com.ermapsh.razorpay.merchant.dto.request.CreateApiRequest;
import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.service.impl.ApiKeyServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/merchant/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyServiceImpl apiKeyServiceImpl;

    @PostMapping()
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> createApiKey(@PathVariable UUID merchantId,
                                                                          @Valid @RequestBody CreateApiRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(201,
                        "API key created successfully",
                        apiKeyServiceImpl.create(merchantId, request),
                        null)
        );
    }

}
