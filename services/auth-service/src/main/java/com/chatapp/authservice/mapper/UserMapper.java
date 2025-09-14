package com.chatapp.authservice.mapper;

import com.chatapp.authservice.model.User;
import com.chatapp.authservice.model.UserStatus;
import com.chatapp.authservice.model.dto.AuthResponse;
import com.chatapp.authservice.model.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapper(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User fromRegisterRequest(final RegisterRequest registerRequest) {
        final User user = new User();

        user.setPhoneNumber(registerRequest.phoneNumber());
        user.setEmail(registerRequest.email());
        user.setName(registerRequest.name());
        user.setPasswordHash(this.passwordEncoder.encode(registerRequest.password()));
        user.setProfilePictureUrl(registerRequest.profilePictureUrl());
        user.setStatus(UserStatus.ACTIVE);

        return user;
    }

    public AuthResponse toAuthResponse(final User user) {
        return new AuthResponse(null, null,
                user.getId(), user.getPhoneNumber(), user.getEmail(), user.getName(),
                user.getProfilePictureUrl(), null, false, user.getMfaEnabled());
    }

    public AuthResponse toAuthResponseWithToken(final User user) {
        return this.toAuthResponseWithToken(null, null, null, user);
    }

    public AuthResponse toAuthResponseWithToken(final String token,
                                                final String refreshToken,
                                                final ZonedDateTime expiresAt,
                                                final User user) {
        return new AuthResponse(token, refreshToken,
                user.getId(), user.getPhoneNumber(), user.getEmail(), user.getName(),
                user.getProfilePictureUrl(), expiresAt, false, user.getMfaEnabled());
    }
}
