package com.ermapsh.razorpay.merchant.mapper;

import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantSignupResponseMapper {

    Merchant toEntityFromSignupRequest(MerchantSignupRequest request);
    MerchantSignupResponse MerchantToSignupResponse(Merchant merchant);
}
