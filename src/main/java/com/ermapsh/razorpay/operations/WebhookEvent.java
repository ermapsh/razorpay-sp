package com.ermapsh.razorpay.operations;

import com.ermapsh.razorpay.common.enums.WebhookEventStatus;
import com.ermapsh.razorpay.common.enums.WebhookEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private WebhookEventType eventType = WebhookEventType.SETTLEMENT_INITIATED;;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload; // Store as JSON string

    @Column(length = 255, nullable = false)
    private String targetUrl;

    @Column(length = 255, nullable = false)
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(length = 100, nullable = false, columnDefinition = "varchar(100) default 'PENDING'")
    private WebhookEventStatus webhookEventStatus;

    @Column(nullable = false)
    private Integer attempts = 0;

    private LocalDateTime nextRetryAt;

    private LocalDateTime lastRetryAt;

    private Integer lastResponseStatusCode;

    @Column(length = 1000)
    private String lastResponseBody;

    private LocalDateTime deliveredAt;

}
