package com.ermapsh.razorpay.payment.dto.response;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse{
    UUID id;
    UUID merchantId;
    String receipt;
    Money amount;
    OrderStatus status;
    Integer attempts;
    Map<String, Object> notes;
    LocalDateTime expiresAt;
    LocalDateTime createdAt;
}