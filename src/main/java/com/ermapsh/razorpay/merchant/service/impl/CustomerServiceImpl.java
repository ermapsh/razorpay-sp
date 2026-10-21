package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.exception.ResourceNotFoundException;
import com.ermapsh.razorpay.merchant.entity.Customer;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.repository.CustomerRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Transactional
    @Override
    public UUID findOrCreate(
            UUID merchantId,
            String email,
            String name,
            String phone) {

        if (email == null || email.isBlank()) {
            return null;
        }

        System.out.println("Before customer lookup");

        Optional<Customer> existing =
                customerRepository.findByMerchant_IdAndEmail(merchantId, email.trim());


        if (existing.isPresent()) {
            log.info("After customer lookup,{}", existing.get().getId());

            System.out.println("Existing customer found: "
                    + existing.get().getId());

            return existing.get().getId();
        }

        System.out.println("No customer found; creating new customer");

        // Continue with your existing create logic.
        return createNew(merchantId, email, name, phone); // Replace with your existing implementation.
    }

    private UUID createNew(UUID merchantId, String email, String name, String phone) {


        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() ->
                new ResourceNotFoundException("Merchant not found: " + merchantId));

        Customer customer = Customer.builder().
                merchant(merchant).
                name(name).
                email(email).
                phone(phone).
                build();

        customer = customerRepository.save(customer);

        return customer.getId();

    }


}
