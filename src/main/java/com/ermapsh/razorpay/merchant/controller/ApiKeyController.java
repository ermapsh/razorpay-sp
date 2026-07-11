package com.ermapsh.razorpay.merchant.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.merchant.dto.request.CreateApiRequest;
import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.dto.response.GetAllApiByMerchant;
import com.ermapsh.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> createApiKey(
            @PathVariable UUID merchantId,
            @Valid @RequestBody CreateApiRequest request) {
        return ApiResponse.created(
                "API key created successfully",
                apiKeyService.create(merchantId, request)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetAllApiByMerchant>>> listByMerchant(@PathVariable UUID merchantId) {
        return ApiResponse.ok(
                "API keys fetched successfully",
                apiKeyService.listByMerchant(merchantId)
        );
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<ApiResponse<Void>> revoke(@PathVariable UUID merchantId, @PathVariable UUID keyId) {
        apiKeyService.revoke(merchantId, keyId);
        return ApiResponse.ok(
                "API key revoke successfully",
                null
        );
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> rotateKey(@PathVariable UUID merchantId, @PathVariable UUID keyId) {
        return ApiResponse.ok(
                "API key rotated successfully",
                apiKeyService.rotateKey(merchantId, keyId)
        );
    }

}
