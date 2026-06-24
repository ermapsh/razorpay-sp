package com.ermapsh.razorpay.payment.entity;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.PaymentMethod;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false, length = 100)
    private String idempotency;

    @Column(length = 100)
    private String bankReference;

    @Embedded
    private Money money;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> methodDetails;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus paymentStatus = PaymentStatus.CREATED;

    @Column(length = 100)
    private String errorCode;

    @Column(length = 255)
    private String errorDescription;

    private LocalDateTime authorizedAt;

    private LocalDateTime capturedAt;

    private LocalDateTime failedAt;

    private LocalDateTime refundedAt;
}
