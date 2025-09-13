package com.chatapp.authservice.controller;

import com.chatapp.authservice.model.User;
import com.chatapp.authservice.model.dto.AuthResponse;
import com.chatapp.authservice.model.dto.LoginRequest;
import com.chatapp.authservice.model.dto.RegisterRequest;
import com.chatapp.authservice.service.PasswordResetService;
import com.chatapp.authservice.service.UserService;
import com.chatapp.authservice.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public AuthController(final UserService userService,
                          final UserSessionService userSessionService,
                          final PasswordResetService passwordResetService) {
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        try {
            final AuthResponse response = userService.registerUser(registerRequest);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid final LoginRequest loginRequest,
                                   final HttpServletRequest httpRequest) {
        try {
            final User user = userService.findActiveUserByIdentifier(loginRequest.phoneNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

            // Verify password (pseudo-code)
            if (!userService.verifyPassword(user, loginRequest.password())) {
                userService.recordFailedLogin(loginRequest.phoneNumber(),
                        httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"),
                        loginRequest.deviceId(), "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }

            // Check if MFA is required
            if (user.getMfaEnabled()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("mfa_required", true, "user_id", user.getId()));
            }

            // Create session and return tokens
            final AuthResponse response = this.userSessionService.createSession(user,
                    loginRequest.deviceId(), loginRequest.deviceName(),
                    loginRequest.deviceType(), httpRequest.getRemoteAddr(),
                    httpRequest.getHeader("User-Agent"), 24, 30);

            userService.updateLastLogin(user.getPhoneNumber(), httpRequest.getRemoteAddr(),
                    httpRequest.getHeader("User-Agent"), loginRequest.deviceId());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String sessionToken = authHeader.replace("Bearer ", "");
        this.userSessionService.revokeSessionByToken(sessionToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        return userSessionService.refreshSession(refreshToken)
                .map(session -> ResponseEntity.ok(Map.of(
                        "session_token", session.getSessionToken(),
                        "expires_at", session.getExpiresAt()
                )))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token")));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        this.passwordResetService.createPasswordResetToken(email);

        return ResponseEntity.ok(Map.of("message", "Password reset instructions sent"));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("new_password");

        if (this.passwordResetService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
        }
    }
}
