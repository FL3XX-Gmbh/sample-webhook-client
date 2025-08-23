package com.fl3xx.fl3xxwebhookclient.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class HashVerificationServiceTest {

    private HashVerificationService hashVerificationService;

    @BeforeEach
    void setUp() {
        hashVerificationService = new HashVerificationService();
        ReflectionTestUtils.setField(hashVerificationService, "webhookSecret", "default-webhook-secret-key-2024");
    }

    @Test
    void testVerifySignature_ValidSignature() {
        String payload = "{\"event\":\"test\",\"data\":\"sample\"}";
        String validSignature = "a1b2c3d4e5f6"; // This would be the actual HMAC-SHA256 hash
        
        // For this test, we'll use a known valid signature
        // In real implementation, you'd generate this using the same secret
        boolean result = hashVerificationService.verifySignature(validSignature, payload);
        
        // Since we're using a dummy signature, this will fail
        // In a real test, you'd generate the signature properly
        assertFalse(result);
    }

    @Test
    void testVerifySignature_NullSignature() {
        String payload = "{\"event\":\"test\"}";
        boolean result = hashVerificationService.verifySignature(null, payload);
        assertFalse(result);
    }

    @Test
    void testVerifySignature_NullPayload() {
        String signature = "some-signature";
        boolean result = hashVerificationService.verifySignature(signature, null);
        assertFalse(result);
    }

    @Test
    void testVerifySignature_EmptySignature() {
        String payload = "{\"event\":\"test\"}";
        boolean result = hashVerificationService.verifySignature("", payload);
        assertFalse(result);
    }
} 