package com.fl3xx.fl3xxwebhookclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fl3xx.fl3xxwebhookclient.service.HashVerificationService;
import com.fl3xx.fl3xxwebhookclient.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WebhookController {

    private final WebhookService webhookService;
    private final HashVerificationService hashVerificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookController(WebhookService webhookService, 
                           HashVerificationService hashVerificationService,
                           ObjectMapper objectMapper) {
        this.webhookService = webhookService;
        this.hashVerificationService = hashVerificationService;
        this.objectMapper = objectMapper;
    }

    /**
     * Endpoint to receive webhook events.
     * 
     * @param signature The webhook signature header
     * @param payload The raw request body as string
     * @return Response indicating success or failure
     */
    @PostMapping("/events")
    public ResponseEntity<String> receiveEvent(@RequestHeader(value = "X-Webhook-Signature", required = false) String signature,
                                             @RequestBody String payload) {
        try {
            // Verify the webhook signature
            if (signature == null || signature.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing webhook signature");
            }

            if (!hashVerificationService.verifySignature(signature, payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid webhook signature");
            }

            // Parse the payload to Map for processing
            Map<String, Object> eventPayload = objectMapper.readValue(payload, Map.class);

            // Process the event asynchronously
            webhookService.processAndStoreEvent(eventPayload);

            // Return success immediately (async processing continues in background)
            return ResponseEntity.ok("Event received and queued for processing");

        } catch (Exception e) {
            // Log error without exposing sensitive information
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing webhook event");
        }
    }
}