package com.ermapsh.razorpay.operations;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID paymentId;

    @Embedded
    @AttributeOverride(name = "amountUnits", column = @Column(name = "gross_settlement_amount", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "gross_settlement_currency", nullable = false, length = 3))
    private Money grossAmount;

    @Embedded
    @AttributeOverride(name = "amountUnits", column = @Column(name = "refund_settlement_amount", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "refund_settlement_currency", nullable = false, length = 3))
    private Money refundAmount;

    @Embedded
    @AttributeOverride(name = "amountUnits", column = @Column(name = "fee_settlement_amount", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "fee_settlement_currency", nullable = false, length = 3))
    private Money feeAmount;

    @Embedded
    @AttributeOverride(name = "amountUnits", column = @Column(name = "gst_settlement_amount", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "gst_settlement_currency", nullable = false, length = 3))
    private Money gstAmount;

    @Embedded
    @AttributeOverride(name = "amountUnits", column = @Column(name = "net_settlement_amount", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "net_settlement_currency", nullable = false, length = 3))
    private Money netAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SettlementStatus status = SettlementStatus.INITIATED;

    @Column(length = 100)
    private String bankReference;

    @Column(length = 100)
    private String errorCode;

    @Column(length = 255)
    private String errorDescription;

    @Column(nullable = false)
    private LocalDateTime processedAt;

}
