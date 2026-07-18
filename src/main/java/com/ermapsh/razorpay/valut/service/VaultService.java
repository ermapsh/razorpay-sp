package com.ermapsh.razorpay.valut.service;

import com.ermapsh.razorpay.valut.dto.request.TokenizeRequest;
import com.ermapsh.razorpay.valut.dto.response.TokenizeResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface VaultService {
    public TokenizeResponse tokenize(@Valid TokenizeRequest request, UUID merchantId);
}
