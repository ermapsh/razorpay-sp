package com.ermapsh.razorpay.payment.repository;

import com.ermapsh.razorpay.payment.dto.response.CreateOrderResponse;
import com.ermapsh.razorpay.payment.entity.Order;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    CreateOrderResponse getById(UUID id, UUID merchantId);
    boolean existsByMerchantIdAndReceipt(UUID merchantId, @Size(max = 100) String receipt);
    Optional<Order> findByIdAndMerchantId(UUID id, UUID merchantId);

    /* Doing for more consistency rather than concurrency */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id= :uuid and o.merchantId= :merchantId")
    Optional<Order> findByIdAndMerchantIdForUpdate(UUID uuid, UUID merchantId);
}