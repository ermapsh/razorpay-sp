package com.ermapsh.razorpay.operations.webhook;

import com.ermapsh.razorpay.common.enums.WebhookEventStatus;
import com.ermapsh.razorpay.operations.entity.WebhookEvent;
import com.ermapsh.razorpay.operations.repository.WebhookEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryExecutor {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final RestClient restClient;
    private final WebhookDlqRecorder webhookDlqRecorder;

    @Value("${webhook.delivery.signature-header:X-Razorypay-Signature}")
    private String signatureHeader;

    private static final List<Duration> BACKOFF = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofMinutes(2), Duration.ofMinutes(8), Duration.ofMinutes(24)
    );

    private final int MAX_ATTEMPTS = 7;

    @Transactional
    public void deliver(UUID webhookEventId) {
        Optional<WebhookEvent> webhookEvent = webhookEventRepository.findById(webhookEventId);

        if (webhookEvent.isEmpty()) {
            log.warn("No webhook event found for this id {}", webhookEventId);
            return;
        }

        WebhookEvent event = webhookEvent.get();

        if (event.getWebhookEventStatus() == WebhookEventStatus.DELIVERED
                || event.getWebhookEventStatus() == WebhookEventStatus.DEAD) {
            log.warn("Cannot deliver the event {} in status: {}",
                    webhookEventId, event.getWebhookEventStatus());
        }

        event.setAttempts(event.getAttempts() + 1);
        event.setLastRetryAt(LocalDateTime.now());

        try {
            var res = restClient.post().uri(event.getTargetUrl()).
                    header(signatureHeader, event.getSignature()).
                    contentType(MediaType.APPLICATION_JSON).
                    body(Map.of(
                            "event", event.getEventType(),
                            "payload", event.getPayload()
                    )).retrieve().toBodilessEntity();

            int statusCode = res.getStatusCode().value();
            event.setLastResponseStatusCode(statusCode);

            if (res.getStatusCode().is2xxSuccessful()) {
                event.setWebhookEventStatus(WebhookEventStatus.SUCCESS);
                event.setDeliveredAt(LocalDateTime.now());
                webhookEventRepository.save(event);
                log.info("Successfully called the merchant for event webhook event: {}", webhookEventId);
                return;
            }

            handleAttemptFailed(event, "HTTP" + statusCode);

        } catch (RestClientException e) {
            event.setLastResponseBody(e.getMessage());
            handleAttemptFailed(event, e.getMessage());
            log.error("Got RestClientException error", e);
        }
    }

    private void handleAttemptFailed(WebhookEvent event, String error) {
        event.setLastResponseBody(error);
        if (event.getAttempts() >= MAX_ATTEMPTS) {
            event.setWebhookEventStatus(WebhookEventStatus.DEAD);
            webhookDlqRecorder.recordAfterAttemptedExhausted(event, error);
        }

        Duration backoff = BACKOFF.get(event.getAttempts() - 1);
        LocalDateTime nextRetryAt = LocalDateTime.now().plus(backoff);
        event.setNextRetryAt(nextRetryAt);
        webhookEventRepository.save(event);

        webhookRetryQueue.enqueue(event.getId(), event.getNextRetryAt());
        log.error("Handling attempt failed for webhook event {} with attempts: {}", event.getId(), event.getAttempts());
    }

}
