package com.ermapsh.razorpay.payment.service.impl;

import com.ermapsh.razorpay.common.enums.OrderStatus;
import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.entity.Payment;
import com.ermapsh.razorpay.payment.gateway.PaymentGatewayRouter;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.repository.OrderRepository;
import com.ermapsh.razorpay.payment.repository.PaymentRepository;
import com.ermapsh.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;


    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        PaymentMethod paymentMethod = request.method();
        /*
        if(paymentMethod == PaymentMethod.CARD){

        }else if(paymentMethod == PaymentMethod.UPI){

        }
        switch (paymentMethod){
            case CARD -> {}

            case UPI -> {}
        }

        our code should be open for modification/close for modification
        if we write such like hardcoding condition then we have to write new things here

        that's why we are going to use
        Strategy Design Pattern
        */


        Order order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId).orElseThrow(()-> {
            throw new ResourceNotFoundException("Order is Invalid");
        });

        if(order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.ATTEMPTED){
            throw new ResourceNotFoundException("Order cannot accept payment in status: "+ order.getStatus());
        }

        order.setStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts()+1);

        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .money(order.getAmount())
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .paymentStatus(PaymentStatus.CREATED)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                savedPayment.getId(),
                request.orderId(),
                merchantId,
                savedPayment.getMoney(),
                savedPayment.getPaymentMethod(),
                savedPayment.getMethodDetails()
        );

        paymentGatewayRouter.initiate(paymentRequest);
        return null;
    }
}
