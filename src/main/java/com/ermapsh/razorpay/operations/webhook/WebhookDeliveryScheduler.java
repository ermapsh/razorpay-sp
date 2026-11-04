package com.ermapsh.razorpay.operations.webhook;

import com.ermapsh.razorpay.common.enums.WebhookEventStatus;
import com.ermapsh.razorpay.operations.entity.WebhookEvent;
import com.ermapsh.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebhookDeliveryScheduler {

    private final WebhookRetryQueue retryQueue;
    private final WebhookEventRepository eventRepository;

    @Value("${app.webhook.delivery.poll-size:100}")
    private int batchSize;

//    Normal Scheduler
    @Scheduled(fixedDelay = 1000)
    public void pollAndDeliver(){
        Set<UUID> dues = retryQueue.pollDue(batchSize);

        if(dues.isEmpty()) return;

        for (UUID webhookEventId: dues){
//            executor.deliver(eventId) // due of executor to make the https call
        }
    }

//    only get pending webhook event from database
    @Scheduled(fixedDelay = 10000)
    public void reconcileFromDatabase(){
        LocalDateTime now = LocalDateTime.now();
        List<WebhookEvent> dues = eventRepository.
                findByWebhookEventStatusAndNextRetryAtBefore(WebhookEventStatus.PENDING, now);

        for (WebhookEvent event: dues){
            retryQueue.enqueueIfAbsent(event.getId(), event.getNextRetryAt());
        }

    }
}
