package com.ermapsh.razorpay.payment.gateway.adapter;

import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.payment.gateway.PaymentAdapter;
import com.ermapsh.razorpay.payment.gateway.PaymentAdapterGatewayRouter;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentRequest;
import com.ermapsh.razorpay.payment.gateway.dto.PaymentResult;
import com.ermapsh.razorpay.payment.processor.PaymentProcessorRouter;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ermapsh.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.ermapsh.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class NetBankingAdapter implements PaymentAdapter {

    private NetBankingPaymentProcessor netbankingPaymentProcessor;
    private PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("initiate Payment with NetBanking PaymentProcessor, paymentId: {}", request.paymentId());

        try {
            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    request.paymentId(),
                    PaymentMethod.NET_BANKING,
                    request.amount(),
                    request.methodDetails()
            );

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (paymentProcessorResponse) {

                case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorRef());

                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());

                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            };
        } catch (Exception e) {
            log.error("Net Banking payment failed :{}", request.paymentId());
            return new PaymentResult.Failure("NET_BANKING_FAILED", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("NBK");
    }
}
