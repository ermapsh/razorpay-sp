package com.ermapsh.razorpay.merchant.mapper;

import com.ermapsh.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.ermapsh.razorpay.merchant.entity.MerchantWebhookConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebhookConfigMapper {

    @Mapping(target = "webhookSecret", source = "rawSecret")
    WebhookConfigResponse toResponse(MerchantWebhookConfig config,
                                     String rawSecret);
}
