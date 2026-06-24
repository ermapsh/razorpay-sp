package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.enums.MerchantStatus;
import com.ermapsh.razorpay.common.enums.UserRole;
import com.ermapsh.razorpay.common.exception.DuplicateResourceException;
import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;
import com.ermapsh.razorpay.merchant.entity.AppUser;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.mapper.MerchantSignupResponseMapper;
import com.ermapsh.razorpay.merchant.repository.AppUserRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantSignupResponseMapper merchantMapper;

    @Override
    @Transactional
    public MerchantSignupResponse signup(MerchantSignupRequest request) {
        // Check if the email already exists
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists "  + request.email());
        }

        // Create a new merchant entity and save it to the database
        /*
        Merchant merchant = Merchant.builder()
                .name(request.name())
                .businessType(request.businessType())
                .businessName(request.businessName())
                .email(request.email())
                .build();
         */

        Merchant merchant = merchantMapper.toEntityFromSignupRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);
        Merchant savedMerchant = merchantRepository.save(merchant);

        // need to create an app user after merchant is created and save it to the database
        AppUser appuser = AppUser.builder()
                .merchant(savedMerchant)
                .email(request.email())
                .passwordHash(request.password()) // TODO encrypt using BcryptPasswordEncoder
                .userRole(UserRole.valueOf("ADMIN"))
                .build();

        appUserRepository.save(appuser);

//        return new MerchantSignupResponse(savedMerchant.getId(), savedMerchant.getName(),
//                savedMerchant.getBusinessType(), savedMerchant.getBusinessName(), savedMerchant.getEmail(), savedMerchant.getStatus());
        return merchantMapper.MerchantToSignupResponse(savedMerchant);
    }
}
