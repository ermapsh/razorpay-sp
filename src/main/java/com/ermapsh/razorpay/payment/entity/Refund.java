package com.ermapsh.razorpay.payment.entity;

import com.ermapsh.razorpay.common.entity.BaseEntity;
import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.RefundStatus;
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
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;

    @Column(nullable = false)
    private UUID merchantId;

    @Embedded
    private Money money;

    @Column(length = 100)
    private String bankReference;

    @Column(length = 100)
    private String errorCode;

    @Column(length = 500)
    private String errorDescription;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus = RefundStatus.PENDING;

    @JdbcTypeCode(SqlTypes.JSON )
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;

    @Column(nullable = false)
    private LocalDateTime SuccessAt;


}
