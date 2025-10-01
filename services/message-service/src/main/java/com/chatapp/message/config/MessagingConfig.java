package com.chatapp.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MessagingConfig {
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        // Add custom interceptors for logging or headers
        restTemplate.getInterceptors().add(
                (request, body, execution) -> execution.execute(request, body)
        );

        return restTemplate;
    }
}
