package com.fl3xx.fl3xxwebhookclient.controller;

import com.fl3xx.fl3xxwebhookclient.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@Import(WebhookControllerTest.TestConfig.class)
public class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebhookService webhookService;

    @Configuration
    static class TestConfig {
        @Bean
        public WebhookService webhookService() {
            return Mockito.mock(WebhookService.class);
        }
    }

    @Test
    public void testReceiveEvent() throws Exception {
        String eventPayload = "{\"event\":\"test\",\"data\":\"sample data\"}";

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventPayload))
                .andExpect(status().isOk())
                .andExpect(content().string("Event received successfully"));

        verify(webhookService, times(1)).processAndStoreEvent(any(Map.class));
    }
}
