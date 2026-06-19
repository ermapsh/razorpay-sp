package com.ermapsh.razorpay.payment.entity;

import com.ermapsh.razorpay.common.enums.PaymentActor;
import com.ermapsh.razorpay.common.enums.PaymentEvent;
import com.ermapsh.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransitionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY) // optional
    @JoinColumn( nullable = false, name = "order_id")
    private OrderRecord orderRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( nullable = false, name = "payment_id")
    private PaymentRecord paymentRecord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentEvent paymentEvent;

    @Column(name="actor", length = 100)
    @Enumerated(EnumType.STRING)
    private PaymentActor actor;

    @Column(nullable = false)
    private LocalDateTime occurredAt;
}
