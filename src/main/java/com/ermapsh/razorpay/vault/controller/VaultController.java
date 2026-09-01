package com.ermapsh.razorpay.vault.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.merchant.security.MerchantContext;
import com.ermapsh.razorpay.vault.dto.request.TokenizeRequest;
import com.ermapsh.razorpay.vault.dto.response.TokenizeResponse;
import com.ermapsh.razorpay.vault.service.VaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vault")
public class VaultController {

    private final VaultService vaultService;
    private final MerchantContext merchantContext;

    @PostMapping("/tokenize")
    public ResponseEntity<ApiResponse<TokenizeResponse>> tokenize(@Valid @RequestBody TokenizeRequest request) {
        return ApiResponse.created(
                "tokenization completed",
                vaultService.tokenize(request, merchantContext.getMerchantId())
        );
    }
}
