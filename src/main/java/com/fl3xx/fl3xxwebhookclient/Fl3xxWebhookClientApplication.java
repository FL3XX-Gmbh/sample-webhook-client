package com.fl3xx.fl3xxwebhookclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Fl3xxWebhookClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Fl3xxWebhookClientApplication.class, args);
    }

}
