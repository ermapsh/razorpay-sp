package com.ermapsh.razorpay.payment.entity;

import com.ermapsh.razorpay.common.entity.Money;
import com.ermapsh.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /* No FK- cross service boundary */
    @Column(nullable = false, name = "merchant_id")
    private UUID merchant;

    //    private String idempotency;
    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(nullable = false)
    private Integer attempts = 0;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(length = 1000, columnDefinition = "jsonb")
    private Map<String, Objects> notes;

    @Column(nullable = false, name = "expires_at")
    private LocalDateTime expiresAt;

}
