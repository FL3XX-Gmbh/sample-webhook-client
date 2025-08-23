package com.fl3xx.fl3xxwebhookclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service for processing and storing webhook events.
 */
@Service
public class WebhookService {

    @Value("${webhook.events.file:events.log}")
    private String eventsFilePath;

    /**
     * Process and store the webhook event to a file.
     * This method is called asynchronously after webhook verification.
     *
     * @param payload The event payload
     */
    @Async("webhookTaskExecutor")
    public void processAndStoreEvent(Map<String, Object> payload) {
        try {
            // Create directory if it doesn't exist
            File file = new File(eventsFilePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                Files.createDirectories(parentDir.toPath());
            }

            // Format the event with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            String eventData = String.format("[%s] %s%n", timestamp, payload.toString());

            // Append to file
            try (FileWriter writer = new FileWriter(eventsFilePath, true)) {
                writer.write(eventData);
            }
            
            // Additional async processing can be added here
            // For example: database operations, external API calls, etc.
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store webhook event", e);
        }
    }
}