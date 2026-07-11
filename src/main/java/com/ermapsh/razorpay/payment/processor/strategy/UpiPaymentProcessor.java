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
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String VPA_CODE_FAIL = "fail@axis";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("vpa").toString() : null;

        // simulation
        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Bank rejected the transaction registration");
        }

        String processorRef = "NBK_PROCESSOR" + RandomizerUtil.randomBase64(16);

        String redirectRef = "http://REDIRECT_BANK.com/" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Success(processorRef, redirectRef);
    }

}
