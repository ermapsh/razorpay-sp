package com.ermapsh.razorpay.payment.statemachine;

import com.ermapsh.razorpay.common.enums.PaymentEvent;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.common.exception.InvalidStateTransitionException;
import com.ermapsh.razorpay.payment.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStateMachine {

//    transition
    private record Transition(PaymentStatus from, PaymentEvent event){}

//    form-via_which_state-to
    private static final Map<Transition, PaymentStatus> TRANSITION_PAYMENT_STATUS_MAP = Map.ofEntries(
        Map.entry(new Transition(PaymentStatus.CREATED, PaymentEvent.AUTHORIZE_ATTEMPT), PaymentStatus.AUTHORIZING),
        Map.entry(new Transition(PaymentStatus.CREATED, PaymentEvent.CANCEL), PaymentStatus.CANCELLED),

        Map.entry(new Transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_SUCCESS), PaymentStatus.AUTHORIZED),
        Map.entry(new Transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_FAIL), PaymentStatus.FAILED),
        Map.entry(new Transition(PaymentStatus.AUTHORIZED, PaymentEvent.CAPTURE_REQUEST), PaymentStatus.CAPTURING),
        Map.entry(new Transition(PaymentStatus.AUTHORIZING, PaymentEvent.CANCEL), PaymentStatus.CANCELLED),
        Map.entry(new Transition(PaymentStatus.AUTHORIZED, PaymentEvent.CANCEL), PaymentStatus.AUTH_EXPIRED),

        Map.entry(new Transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_SUCCESS), PaymentStatus.CAPTURED),
        Map.entry(new Transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_FAIL), PaymentStatus.AUTHORIZED),
        Map.entry(new Transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_TIMEOUT), PaymentStatus.FAILED),

        Map.entry(new Transition(PaymentStatus.CAPTURED, PaymentEvent.REFUND_INIT), PaymentStatus.PARTIALLY_REFUNDED),
        Map.entry(new Transition(PaymentStatus.CAPTURED, PaymentEvent.SETTLE), PaymentStatus.SETTLED),
        Map.entry(new Transition(PaymentStatus.PARTIALLY_REFUNDED, PaymentEvent.REFUND_COMPLETE), PaymentStatus.REFUNDED),
        Map.entry(new Transition(PaymentStatus.CAPTURED, PaymentEvent.REFUND_COMPLETE), PaymentStatus.REFUNDED),
        Map.entry(new Transition(PaymentStatus.SETTLED, PaymentEvent.REFUND_INIT), PaymentStatus.PARTIALLY_REFUNDED)
);

    public PaymentStatus transition(PaymentStatus from, PaymentEvent event){
        PaymentStatus to = TRANSITION_PAYMENT_STATUS_MAP.get(new Transition(from, event));

        if(to == null){
            throw new InvalidStateTransitionException(from.name(), event.name());
        }
        return to;
    }
}
