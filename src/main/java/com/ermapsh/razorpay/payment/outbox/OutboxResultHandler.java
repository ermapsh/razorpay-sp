package com.ermapsh.razorpay.payment.outbox;

import com.ermapsh.razorpay.common.enums.OutboxStatus;
import com.ermapsh.razorpay.payment.entity.OutboxEvent;
import com.ermapsh.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxResultHandler {

    private final OutboxEventRepository outboxEventRepository;
    private final Integer MAX_ATTEMPTS = 3;

    @Transactional
    public void handleEventPublished(OutboxEvent event) {
        event.setOutboxStatus(OutboxStatus.PUBLISH);
        event.setPublishedAt(LocalDateTime.now());
        outboxEventRepository.save(event);
    }

    @Transactional
    public void handleEventFailed(OutboxEvent event, String msg) {
        event.setAttempts(event.getAttempts() + 1);
        event.setLastError(msg.length() < 1000 ? msg : msg.substring(0, 1000));
        if(event.getAttempts() >= MAX_ATTEMPTS){
                event.setOutboxStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(event);
    }
}
