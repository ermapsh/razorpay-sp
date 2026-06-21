package com.ermapsh.razorpay.operations;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.EmbeddedColumnNaming;

@Entity
@Table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPayment {

    @EmbeddedId
    private SettlementPaymentId paymentId;

    @MapsId("settlementId") // for referencing the parent entity's primary key
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}
