package com.ermapsh.razorpay.payment.statemachine;

import com.ermapsh.razorpay.common.enums.PaymentActor;
import com.ermapsh.razorpay.common.enums.PaymentEvent;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.payment.entity.Payment;
import com.ermapsh.razorpay.payment.entity.PaymentTransitionLog;
import com.ermapsh.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent) {

        PaymentStatus fromStatus = payment.getPaymentStatus();

        PaymentStatus toStatus = paymentStateMachine.transition(fromStatus, paymentEvent);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(fromStatus)
                .paymentEvent(paymentEvent)
                .toStatus(toStatus)
                .actor(PaymentActor.SYSTEM)
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setPaymentStatus(toStatus);

        paymentTransitionLogRepository.save(log);

        return toStatus;
    }
}
