package com.chatapp.authservice.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendPasswordResetEmail(String email, String token) {
        // In a real implementation, this would send an actual email
        // For now, we'll just log it
        System.out.println("Password reset token for " + email + ": " + token);
        System.out.println("Reset URL: https://yourapp.com/reset-password?token=" + token);
    }

    public void sendEmailVerification(String email, String token) {
        System.out.println("Email verification token for " + email + ": " + token);
        System.out.println("Verification URL: https://yourapp.com/verify-email?token=" + token);
    }
}
