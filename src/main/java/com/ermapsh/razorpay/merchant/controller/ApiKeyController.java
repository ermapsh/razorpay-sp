package com.ermapsh.razorpay.merchant.controller;

import com.ermapsh.razorpay.common.enums.ApiResponse;
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
@RequestMapping("/v1/merchant/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> createApiKey(@PathVariable UUID merchantId,
                                                                          @Valid @RequestBody CreateApiRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(201,
                        "API key created successfully",
                        apiKeyService.create(merchantId, request),
                        null)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetAllApiByMerchant>>> listByMerchant(@PathVariable UUID merchantId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(200,
                        "API keys fetched successfully",
                        apiKeyService.listByMerchant(merchantId),
                        null)
        );
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<ApiResponse<?>> revoke(@PathVariable UUID merchantId, @PathVariable UUID keyId){
        apiKeyService.revoke(merchantId, keyId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(200,
                        "API key revoke successfully",
                        null,
                        null)
        );
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiResponse<?>> rotateKey(@PathVariable UUID merchantId, @PathVariable UUID keyId){

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(200,
                        "API key rotated successfully",
                        apiKeyService.rotateKey(merchantId, keyId),
                        null)
        );
    }

}
