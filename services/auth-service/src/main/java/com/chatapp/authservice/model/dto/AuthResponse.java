package com.chatapp.authservice.model.dto;

import java.time.ZonedDateTime;

public record AuthResponse(String token,
                           String refreshToken,
                           Long userId,
                           String phoneNumber,
                           String email,
                           String name,
                           String profilePictureUrl,
                           ZonedDateTime expiresAt,
                           boolean mfaRequired,
                           boolean mfaEnabled) {
}
