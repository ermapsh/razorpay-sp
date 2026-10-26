package com.ermapsh.razorpay.payment.outbox;


import com.ermapsh.razorpay.common.config.KafkaProperties;
import com.ermapsh.razorpay.common.enums.OutboxStatus;
import com.ermapsh.razorpay.payment.entity.OutboxEvent;
import com.ermapsh.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler resultHandler;

    @Scheduled(fixedDelay = 5000)
    public void poll() {

        List<OutboxEvent> pendingEvents = outboxEventRepository.findByOutboxStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        log.info("-----gotten pendingEvents outbox event: {}-----", pendingEvents.stream().count());
        for (OutboxEvent event : pendingEvents) {
            try {
                String topic = kafkaProperties.topicFor(event.getAggregateType());
                String key = extractMerchantId(event.getPayload());

                Map<String, Object> envelop = Map.of(
                        "eventType", event.getEventType(),
                        "aggregateType", event.getAggregateType(),
                        "aggregateId", event.getAggregateId().toString(),
                        "data", event.getPayload()
                );

                kafkaTemplate.send(topic, key, envelop)
                        .get(5, TimeUnit.SECONDS);

                resultHandler.handleEventPublished(event);
            } catch (Exception e) {
                log.error("Outbox event failed, eventId: {}, attempts: {}", event.getId(), event.getAttempts());
                resultHandler.handleEventFailed(event, e.getMessage());
            }
        }

    }

    private String extractMerchantId(Map<String, Object> payload) {
        Object val = payload.get("merchantId");
        return val != null ? val.toString() : "unknown";
    }
}
