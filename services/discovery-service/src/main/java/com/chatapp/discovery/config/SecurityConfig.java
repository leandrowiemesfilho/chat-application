package com.chatapp.discovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (can interfere with Eureka)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/eureka/**").permitAll()     // Allow Eureka endpoints
                        .requestMatchers("/actuator/**").permitAll()   // Allow actuator endpoints
                        .anyRequest().authenticated()                  // All other requests need authentication
                )

                // Enable HTTP Basic authentication
                .httpBasic(Customizer.withDefaults());

        return http.build();

    }
}
