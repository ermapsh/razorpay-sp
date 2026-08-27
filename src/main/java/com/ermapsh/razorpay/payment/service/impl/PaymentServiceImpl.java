package com.ermapsh.razorpay.payment.service.impl;

import com.ermapsh.razorpay.common.enums.OrderStatus;
import com.ermapsh.razorpay.common.enums.PaymentEvent;
import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.common.util.RandomizerUtil;
import com.ermapsh.razorpay.payment.dto.request.PaymentInitRequest;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.entity.Payment;
import com.ermapsh.razorpay.payment.gateway.PaymentAdapterGatewayRouter;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import com.ermapsh.razorpay.payment.mapper.PaymentMapper;
import com.ermapsh.razorpay.payment.repository.OrderRepository;
import com.ermapsh.razorpay.payment.repository.PaymentRepository;
import com.ermapsh.razorpay.payment.service.PaymentService;
import com.ermapsh.razorpay.payment.statemachine.PaymentTransitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAdapterGatewayRouter paymentAdapterGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse initiate(@RequestHeader("merchantId") UUID merchantId, @Valid PaymentInitRequest request) {
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


        log.info("in PaymentServiceImpl {}", request.method());
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
                .idempotency(UUID.randomUUID().toString())
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .paymentStatus(PaymentStatus.CREATED)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.warn("savedPayment {}", savedPayment.getId());

        PaymentRequest paymentRequest = new PaymentRequest(
                savedPayment.getId(),
                order.getId(),
                merchantId,
                savedPayment.getMoney(),
                savedPayment.getPaymentMethod(),
                savedPayment.getMethodDetails()
        );

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);


        PaymentResult result = paymentAdapterGatewayRouter.initiate(paymentRequest); // it will choose the payment adapter -> and adapter will choose payment processor
        switch (result) {
            case PaymentResult.Pending(String registrationRef) -> payment.setProcessorReference(registrationRef);
            case PaymentResult.Failure(String errorCode, String errorDescription) -> {
//                payment.setPaymentStatus(PaymentStatus.FAILED);

                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
            }
            case PaymentResult.Success(String bankReference) -> {

            }
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }

        orderRepository.save(order);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).
                orElseThrow(()->  new ResourceNotFoundException("Payment not found {}"+ paymentId));

//        payment.setPaymentStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
        PaymentResult paymentResult = paymentAdapterGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

        if(paymentResult instanceof PaymentResult.Success success){

            log.info("Payment captured, paymentId: {}", paymentId);
//            payment.setPaymentStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());

        }else if(paymentResult instanceof PaymentResult.Failure(String errorCode, String errorDescription)){

//            payment.setPaymentStatus(PaymentStatus.AUTHORIZED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
            log.info("Payment captured failed, paymentId: {}", paymentId);
            log.info("Payment captured failed, error : {}", errorDescription);

        }

//        TODO send an outbox (kafka event)

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, Boolean approve, String bankRef, String simBankErrorCode, String simulatedBankDecline) {

         Payment payment = paymentRepository.findById(paymentId).orElseThrow(()->
                new ResourceNotFoundException("Payment Not found: " + paymentId)
         );

        if(payment.getPaymentStatus() != PaymentStatus.AUTHORIZING){
            log.warn("payment is not in authorized state, PaymentId:{}, status:{}", paymentId, payment.getPaymentStatus());
            return;
        }

        Order order = payment.getOrder();
        if(approve){
            /* auto capturing here */
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());


            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentAdapterGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

            if(captureResult instanceof PaymentResult.Success(String bankReference)){
                log.info("success result fo resolve authorization, bank: {}", bankReference);
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                order.setStatus(OrderStatus.PAID);
            }else if(captureResult instanceof PaymentResult.Failure(String errorCode, String errorDescription)){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
            }

        }else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(simBankErrorCode);
            payment.setErrorDescription(simulatedBankDecline);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        // TODO outbox kafka event
    }
}
























