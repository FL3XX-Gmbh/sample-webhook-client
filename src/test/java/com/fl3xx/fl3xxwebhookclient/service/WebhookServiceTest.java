package com.fl3xx.fl3xxwebhookclient.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebhookServiceTest {

    private WebhookService webhookService;
    
    @TempDir
    Path tempDir;
    
    private File tempFile;

    @BeforeEach
    public void setUp() {
        webhookService = new WebhookService();
        tempFile = tempDir.resolve("test-events.log").toFile();
        ReflectionTestUtils.setField(webhookService, "eventsFilePath", tempFile.getAbsolutePath());
    }
    
    @AfterEach
    public void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testProcessAndStoreEvent() throws Exception {
        // Create test payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "test-event");
        payload.put("data", "test-data");
        
        // Process the event
        webhookService.processAndStoreEvent(payload);
        
        // Verify file was created and contains the event data
        assertTrue(tempFile.exists(), "Event log file should be created");
        
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertTrue(lines.size() > 0, "File should contain at least one line");
        
        String line = lines.get(0);
        assertTrue(line.contains("test-event"), "Line should contain event name");
        assertTrue(line.contains("test-data"), "Line should contain event data");
    }
}