package com.ermapsh.razorpay.merchant.dto.request;

import com.ermapsh.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.*;

public record UpdateWebhookConfigRequest(

        @NotBlank(message = "Webhook URL is required")
        @Size(max = 500)
        @Pattern(regexp = "^http?://.+", message = "Webhook URL must be valid http(s) URL")
        String targetUrl,

        @Size(max = 100)
        String eventTypes
) {
}
