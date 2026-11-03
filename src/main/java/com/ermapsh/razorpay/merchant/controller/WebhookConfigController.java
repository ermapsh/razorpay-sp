
package com.ermapsh.razorpay.merchant.controller;

import com.ermapsh.razorpay.common.dto.ApiResponse;
import com.ermapsh.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.ermapsh.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.ermapsh.razorpay.merchant.security.MerchantContext;
import com.ermapsh.razorpay.merchant.service.WebhookConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/webhook")
@RequiredArgsConstructor
public class WebhookConfigController {

    private final WebhookConfigService webhookConfigService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiResponse<WebhookConfigResponse>> create(
            @Valid @RequestBody UpdateWebhookConfigRequest request) {

        UUID merchantId = merchantContext.getMerchantId();

        WebhookConfigResponse response =
                webhookConfigService.create(merchantId, request);

        return ApiResponse.created(
                "Webhook configuration created successfully",
                response
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WebhookConfigResponse>>> list() {

        UUID merchantId = merchantContext.getMerchantId();

        List<WebhookConfigResponse> response =
                webhookConfigService.list(merchantId);

        return ApiResponse.ok(
                "Webhook configurations fetched successfully",
                response
        );
    }

    @GetMapping("/{configId}")
    public ResponseEntity<ApiResponse<WebhookConfigResponse>> getById(
            @PathVariable UUID configId) {

        UUID merchantId = merchantContext.getMerchantId();

        WebhookConfigResponse response =
                webhookConfigService.getById(merchantId, configId);

        return ApiResponse.ok(
                "Webhook configuration fetched successfully",
                response
        );
    }

    @PutMapping("/{configId}")
    public ResponseEntity<ApiResponse<WebhookConfigResponse>> update(
            @PathVariable UUID configId,
            @Valid @RequestBody UpdateWebhookConfigRequest request) {

        UUID merchantId = merchantContext.getMerchantId();

        WebhookConfigResponse response =
                webhookConfigService.update(merchantId, configId, request);

        return ApiResponse.ok(
                "Webhook configuration updated successfully",
                response
        );
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID configId) {

        UUID merchantId = merchantContext.getMerchantId();

        webhookConfigService.delete(merchantId, configId);

        return ApiResponse.of(
                org.springframework.http.HttpStatus.NO_CONTENT,
                "Webhook configuration deleted successfully",
                null
        );
    }
}
