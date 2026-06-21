package com.ermapsh.razorpay.operations;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "dlq_event")
public class DLQEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private  UUID merchantId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "webhook_event_id", nullable = false)
    private WebhookEvent webhookEvent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload; // Store as JSON string

    @Column(length = 255, nullable = false)
    private String finalError;

    @Column
    private LocalDateTime movedToDLQAt;

    @Column
    private LocalDateTime ReplayedAt;

}
