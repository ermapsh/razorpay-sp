package com.ermapsh.razorpay.common.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class RandomizerUtil {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String randomBase64() {
        return randomBase64(24);
    }

    public static String randomBase64(int length) {
        byte[] buf = new byte[length];
        SECURE_RANDOM.nextBytes(buf);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(buf);
    }
}
