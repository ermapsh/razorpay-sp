package com.ermapsh.razorpay.payment.controller;

import com.ermapsh.razorpay.common.enums.ApiResponse;
import com.ermapsh.razorpay.payment.dto.request.CreateOrderRequest;
import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private UUID merchantId = UUID.fromString("de168599-e512-4493-b119-8151fdcbac69"); // TODO: replace it with MerchantContext

    @PostMapping("")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(@RequestBody @Valid CreateOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(
                        HttpStatus.CREATED.value(),
                        "order created successfully",
                        orderService.createOrder(merchantId, request),
                        null
                )

        );
    }
}
