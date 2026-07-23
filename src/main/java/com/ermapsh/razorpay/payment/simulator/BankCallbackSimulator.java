package com.ermapsh.razorpay.payment.simulator;

import com.ermapsh.razorpay.common.enums.ChaosMode;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import com.ermapsh.razorpay.common.util.RandomizerUtil;
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
    public void processCallbacks() {

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRepository.findByPaymentStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow); // now we have to process this payements

        if (candidates.isEmpty()) return;

        for (Payment payment : candidates) {
            simulateCallback(payment);
        }
    }

    public void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig config = simulatorConfig.configFor(payment.getPaymentMethod());

        LocalDateTime dueAt = atDueTime(payment, config);

        if (LocalDateTime.now().isBefore(dueAt)) {
            return;
        }
        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode) {
            case NORMAL, SLOW -> {
                resolve(payment, shouldApprove(payment, config));
            }
            case SUCCESS -> {
                resolve(payment, true);
            }
            case FAILURE -> {
                resolve(payment, false);
            }
            case TIMEOUT -> {
                log.warn("BackCallback simulator: Payment Timeout out");
                resolve(payment, false);
            }
        }

    }

    private void resolve(Payment payment, Boolean approve) {
        if (approve) {
            String bankRef = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Decline");
        }
    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodSimulatorConfig.getMinDelaySeconds();
    }

    private LocalDateTime atDueTime(Payment payment, SimulatorConfig.MethodSimulatorConfig config) {
        /* Due-time for this payment that we achieve here -- after this due-time we can process this payment */
        int range = config.getMaxDelaySeconds() - config.getMinDelaySeconds();
        int delaySeconds = config.getMinDelaySeconds() + Math.abs(payment.getId().hashCode() % (range + 1));

        if (simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delaySeconds *= 2;
        }
        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

}
