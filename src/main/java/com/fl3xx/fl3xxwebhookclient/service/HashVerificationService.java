package com.fl3xx.fl3xxwebhookclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Service for verifying webhook hash signatures.
 */
@Service
public class HashVerificationService {

    @Value("${webhook.secret}")
    private String webhookSecret;

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Verify the webhook signature against the payload.
     *
     * @param signature The signature from the webhook header
     * @param payload   The webhook payload as string
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature(String signature, String payload) {
        if (signature == null || payload == null) {
            return false;
        }

        try {
            String expectedSignature = generateSignature(payload);
            return signature.equals(expectedSignature);
        } catch (Exception e) {
            // Log the error but don't expose sensitive information
            System.err.println("Error verifying webhook signature: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate HMAC-SHA256 signature for the given payload.
     *
     * @param payload The payload to sign
     * @return The hex-encoded signature
     * @throws NoSuchAlgorithmException if HMAC-SHA256 is not available
     * @throws InvalidKeyException if the secret key is invalid
     */
    private String generateSignature(String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }
} 