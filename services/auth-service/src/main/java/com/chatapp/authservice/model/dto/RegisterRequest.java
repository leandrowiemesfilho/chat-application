package com.chatapp.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(@NotBlank(message = "Phone number is required")
                              @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
                              String phoneNumber,
                              String email,
                              @NotBlank(message = "Name is required")
                              String name,
                              @NotBlank(message = "Password is required")
                              String password,
                              String profilePictureUrl) {
}
