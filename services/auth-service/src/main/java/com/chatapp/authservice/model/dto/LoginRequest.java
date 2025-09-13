package com.chatapp.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "Phone number or email is required")
                           String phoneNumber,
                           @NotBlank(message = "Password is required")
                           String password,
                           String deviceId,
                           String deviceName,
                           String deviceType,
                           String mfaCode) {
}
