package com.ermapsh.razorpay.payment.mapper;

import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateOrderResponseMapper {

    CreateOrderResponse toResponse(Order order);
}
