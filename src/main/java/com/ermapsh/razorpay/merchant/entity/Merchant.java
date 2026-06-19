package com.ermapsh.razorpay.merchant.entity;

import com.ermapsh.razorpay.common.enums.BusinessType;
import com.ermapsh.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private BusinessType businessType;

    @Column(unique = true, length = 50)
    private String businessName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false, length = 200)
    private String websiteURL;

    @Column(unique = true, length = 15, nullable = false)
    private String contactNumber;

    @Column(unique = true, length = 20)
    private String panId;

    @Column(unique = true, length = 20)
    private String gstId;

    @Column(length = 200, nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(unique = true)
    private String settlementBankAccount;

    @Column(unique = false)
    private String settlementBankIFSC;

    @Column(unique = true)
    private String settlementBankAccountHolderName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

}
