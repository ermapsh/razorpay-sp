package com.ermapsh.razorpay.operations.webhook;

import com.ermapsh.razorpay.common.dto.WebhookTarget;
import com.ermapsh.razorpay.common.enums.WebhookEventStatus;
import com.ermapsh.razorpay.common.enums.WebhookEventType;
import com.ermapsh.razorpay.common.util.SignerUtil;
import com.ermapsh.razorpay.merchant.api.MerchantWebhookApi;
import com.ermapsh.razorpay.operations.entity.WebhookEvent;
import com.ermapsh.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {
    private final WebhookEventRepository webhookEventRepository;

    private final MerchantWebhookApi merchantWebhookApi;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookRetryQueue retryQueue;
    private final WebhookDlqRecorder recorder;

    @KafkaListener(topics = {
            "${app.kafka.topics.payment:payment.events}",
            "${app.kafka.topics.order:order.events}",
            "${app.kafka.topics.refund:refund.events}",
            "${app.kafka.topics.settlement:settlement.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String, Object>> record, Acknowledgment ack) {
        try {
            Map<String, Object> envelope = record.value();
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            String eventType = (String) envelope.get("eventType");

            Object merchantIdRaw = data.get("merchantId");
            if(merchantIdRaw == null){
                log.warn("No merchantId was found, skipping event: {}", eventType);
                ack.acknowledge();
            }

            UUID merchantId = UUID.fromString(merchantIdRaw.toString());

            List<WebhookTarget> targets = merchantWebhookApi.getActiveConfigsForEvent(merchantId, eventType);

            if(targets.isEmpty()){
                log.debug("No webhook target was found, skipping the event: {}", eventType);
                ack.acknowledge();
                return;
            }

            Map<String, Object> signatureData = Map.of(
                    "event", eventType,
                    "payload", data
            );
            String signatureJson = objectMapper.writeValueAsString(signatureData);

            for(WebhookTarget target: targets){
                String signature = signerUtil.sign(signatureJson, target.webhookSecret());

                WebhookEvent webhookEvent = WebhookEvent.builder().
                        merchantId(merchantId).
                        eventType(WebhookEventType.valueOf(eventType)).
                        payload(data).
                        targetUrl(target.targetUrl()).
                        signature(signature).
                        webhookEventStatus(WebhookEventStatus.PENDING).
                        nextRetryAt(LocalDateTime.now()).
                        build();

                webhookEvent = webhookEventRepository.save(webhookEvent);

                retryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());
            }

            ack.acknowledge();
        } catch (DataAccessException | CannotCreateTransactionException e) {
            log.error("Webhook consumer failed due to DB Down to process the record, offset {}: ", record.offset());
        } catch (Exception logicError) {
            log.error("Webhook consumer failed while process {}", logicError.getMessage());
            recorder.recordConsumerFailed(record, logicError.getMessage());
            ack.acknowledge();
        }
    }
}
