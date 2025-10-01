package com.chatapp.message.config;

import com.chatapp.security.service.EncryptionService;
import com.chatapp.security.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public EncryptionService encryptionService() {
        return new EncryptionService();
    }
}
