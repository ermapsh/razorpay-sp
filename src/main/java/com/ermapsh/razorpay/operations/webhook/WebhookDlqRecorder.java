package com.ermapsh.razorpay.operations.webhook;

import com.ermapsh.razorpay.common.enums.WebhookEventStatus;
import com.ermapsh.razorpay.operations.entity.DLQEvent;
import com.ermapsh.razorpay.operations.entity.WebhookEvent;
import com.ermapsh.razorpay.operations.repository.DLQEventRepository;
import com.ermapsh.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDlqRecorder {

    private final WebhookEventRepository webhookEventRepository;
    private final DLQEventRepository dlqEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAfterAttemptedExhausted(WebhookEvent event, String finalError) {
        event.setWebhookEventStatus(WebhookEventStatus.DEAD);
        webhookEventRepository.save(event);

        DLQEvent dlqEvent = DLQEvent.builder()
                .webhookEvent(event)
                .finalError(finalError)
                .payload(event.getPayload())
                .build();

        dlqEventRepository.save(dlqEvent);


    }

    public void recordConsumerFailed(ConsumerRecord<String, Map<String, Object>> record, String error) {
        Map<String, Object> envelop = record.value();

        UUID merchantId = null;

        try {
            Map<String, Object> data = (Map<String, Object>) envelop.get("data");
            Object merchantIdRaw = data != null ? data.get("merchantId") : null;

            if (merchantIdRaw != null) {
                merchantId = UUID.fromString(merchantIdRaw.toString());
            }
        } catch (Exception ignored) {

        }

        assert merchantId != null;
        DLQEvent dlqEvent = DLQEvent.builder()
                .merchantId(merchantId)
                .webhookEvent(null)
                .finalError(merchantId.toString())
                .payload(envelop != null ? envelop : null)
                .build();

        dlqEventRepository.save(dlqEvent);
    }
}
