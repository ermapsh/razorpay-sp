package com.ermapsh.razorpay.payment.simulator;

import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.payment.entity.Payment;
import com.ermapsh.razorpay.payment.repository.PaymentRepository;
import com.ermapsh.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks(){

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates =  paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow); // now we have to process this payements

        if(candidates.isEmpty()) return;

        for(Payment payment: candidates){
            simulateCallback(payment);
        }
    }


    public void simulateCallback(Payment payment){

    }
}
