package com.ermapsh.razorpay.merchant.service.impl;

import com.ermapsh.razorpay.common.enums.UserRole;
import com.ermapsh.razorpay.common.exception.DuplicateResourceException;
import com.ermapsh.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ermapsh.razorpay.merchant.dto.response.MerchantSignupResponse;
import com.ermapsh.razorpay.merchant.entity.AppUser;
import com.ermapsh.razorpay.merchant.entity.Merchant;
import com.ermapsh.razorpay.merchant.repository.AppUserRepository;
import com.ermapsh.razorpay.merchant.repository.MerchantRepository;
import com.ermapsh.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public MerchantSignupResponse signup(MerchantSignupRequest request) {
        // Check if the email already exists
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists "  + request.email());
        }

        // Create a new merchant entity and save it to the database
        var merchant = Merchant.builder()
                .name(request.name())
                .businessType(request.businessType())
                .businessName(request.businessName())
                .email(request.email())
                .build();

        var savedMerchant = merchantRepository.save(merchant);

        // need to create an app user after merchant is created and save it to the database
        var appuser = AppUser.builder()
                .merchant(savedMerchant)
                .email(request.email())
                .passwordHash(request.password()) // TODO encrypt using BcryptPasswordEncoder
                .userRole(UserRole.valueOf("ADMIN"))
                .build();

        appUserRepository.save(appuser);
        return new MerchantSignupResponse(savedMerchant.getId(), savedMerchant.getName(),
                savedMerchant.getBusinessType(), savedMerchant.getBusinessName(), savedMerchant.getEmail(), savedMerchant.getStatus());

    }

}
