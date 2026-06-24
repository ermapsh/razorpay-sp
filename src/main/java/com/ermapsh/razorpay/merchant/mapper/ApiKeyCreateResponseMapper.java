package com.ermapsh.razorpay.merchant.mapper;

import com.ermapsh.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ermapsh.razorpay.merchant.dto.response.GetAllApiByMerchant;
import com.ermapsh.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyCreateResponseMapper {
    ApiKeyCreateResponse toResponse(ApiKey apiKey);
    List<GetAllApiByMerchant> toResponseList(List<ApiKey> apiKeys);
}
