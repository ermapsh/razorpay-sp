package com.ermapsh.razorpay.merchant.entity;

import com.ermapsh.razorpay.common.entity.BaseEntity;
import com.ermapsh.razorpay.common.enums.BusinessType;
import com.ermapsh.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_merchant_status", columnList = "status"),
        }
)
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private BusinessType businessType;

    @Column(unique = true, length = 50, nullable = false)
    private String businessName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, length = 200)
    private String websiteURL;

    @Column(length = 15)
    private String contactNumber;

    @Column(length = 20)
    private String panId;

    @Column(length = 20)
    private String gstId;

    @Column(length = 200, nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(unique = true)
    private String settlementBankAccount;

    @Column
    private String settlementBankIFSC;

    @Column(unique = true)
    private String settlementBankAccountHolderName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
