package com.ermapsh.razorpay.payment.processor.strategy;

import com.ermapsh.razorpay.common.util.RandomizerUtil;
import com.ermapsh.razorpay.payment.processor.PaymentProcessor;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
// payment processor call here different type of cards here visa, mastercard and many more
public class CardPaymentProcessor implements PaymentProcessor {

    public final String PAN_CARD_DECLINED = "4000000000000002";
    public final String PAN_CARD_EXPIRED = "4000000000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if(PAN_CARD_DECLINED.equals(pan)){
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined by the bank");
        }

        if(PAN_CARD_EXPIRED.equals(pan)){
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        }

        String processorRef = "CARD_PROCESSOR" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
