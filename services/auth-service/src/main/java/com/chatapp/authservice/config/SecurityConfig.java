package com.chatapp.authservice.config;

import com.chatapp.security.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
}
