package com.chatapp.authservice.model.dto;

import jakarta.validation.constraints.Email;

public record PasswordResetRequest(String token,
                                   @Email
                                   String email,
                                   String newPassword) {
}
