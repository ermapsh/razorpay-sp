package com.ermapsh.razorpay.operations.entity;

import com.ermapsh.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "dlq_event")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DLQEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private  UUID merchantId;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "webhook_event_id", nullable = true)
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
