package com.ermapsh.razorpay.operations.entity;


import com.ermapsh.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    private SettlementPaymentId paymentId;

    @MapsId("settlementId") // for referencing the parent entity's primary key
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}
