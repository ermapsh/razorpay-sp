package com.ermapsh.razorpay.payment.service.impl;

import com.ermapsh.razorpay.common.enums.OrderStatus;
import com.ermapsh.razorpay.common.exception.BusinessRuleViolationException;
import com.ermapsh.razorpay.common.exception.DuplicateResourceException;
import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.payment.dto.request.CreateOrderRequest;
import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.dto.response.PaymentResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import com.ermapsh.razorpay.payment.entity.Payment;
import com.ermapsh.razorpay.payment.mapper.PaymentMapper;
import com.ermapsh.razorpay.payment.repository.OrderRepository;
import com.ermapsh.razorpay.payment.repository.PaymentRepository;
import com.ermapsh.razorpay.payment.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    @Value("${payment.order.default-order-expiry-minutes}")
    private int defaultOrderExpiryMinutes;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final PaymentMapper paymentMapper;


    @Override
    @Transactional
    public CreateOrderResponse createOrder(UUID merchantId, CreateOrderRequest request) {
        System.out.println("${defaultOrderExpiryMinutes}");
        if (request.receipt() != null &&
                orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            throw new DuplicateResourceException("Order with receipt already exists" + request.receipt());
        }

        System.out.println(defaultOrderExpiryMinutes);

        Order newOrder = Order.builder().
                receipt(request.receipt()).
                amount(request.amount()).
                notes(request.notes()).

                expiresAt(request.expiresAt() != null ?  request.expiresAt(): LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes)).
                merchantId(merchantId).
                build();

        Order savedOrder = orderRepository.save(newOrder);

        // TODO: publish kafka event about order creation

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getMerchantId(),
                savedOrder.getReceipt(),
                savedOrder.getAmount(),
                savedOrder.getStatus(),
                savedOrder.getAttempts(),
                savedOrder.getNotes(),
                savedOrder.getExpiresAt(),
                null
        );
    }

    private Order getOrder(UUID orderId, UUID merchantId){
        return  orderRepository.findByIdAndMerchantId(orderId, merchantId).orElseThrow(()->
                new ResourceNotFoundException("Order Id not found associate with merchant"));
    }

    @Override
    public CreateOrderResponse getOrderById(UUID merchantId, UUID orderId) {
        Order order = getOrder(orderId, merchantId);
        System.out.println("order="+ order);
        return modelMapper.map(order, CreateOrderResponse.class);
    }

    @Override
    @Transactional
    public CreateOrderResponse cancel(UUID merchantId, UUID orderId) {
        Order order = getOrder(orderId, merchantId);
        if((order.getStatus() == OrderStatus.CANCELLED) || (order.getStatus() == OrderStatus.PAID) ){
            throw new BusinessRuleViolationException(400, "Order cannot be cancelled because it is already " + order.getStatus().name());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return modelMapper.map(order, CreateOrderResponse.class);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        Order order = getOrder(orderId, merchantId);
        List<Payment> list = paymentRepository.findByOrder(order);
        return paymentMapper.toReponseList(list);
//        return list.stream().map((item)->paymentMapper.toResponse(item)).toList();

//                new PaymentResponse(
//                item.getId(),
//                item.getOrder().getId(),
//                item.getMerchantId(),
//                item.getIdempotency(),
//                item.getBankReference(),
//                item.getMoney(),
//                item.getPaymentMethod(),
//                item.getMethodDetails(),
//                item.getPaymentStatus(),
//                item.getErrorCode(),
//                item.getErrorDescription(),
//                item.getAuthorizedAt(),
//                item.getCapturedAt(),
//                item.getFailedAt(),
//                item.getRefundedAt()
//        )

    }

}
