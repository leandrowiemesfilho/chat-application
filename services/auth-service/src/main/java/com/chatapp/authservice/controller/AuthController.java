package com.chatapp.authservice.controller;

import com.chatapp.authservice.exception.InvalidCredentialsException;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.model.dto.AuthResponse;
import com.chatapp.authservice.model.dto.LoginRequest;
import com.chatapp.authservice.model.dto.PasswordResetRequest;
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
@RequestMapping("/api/v1/auth")
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
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        final AuthResponse response = this.userService.registerUser(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader("User-Agent") final String userAgent,
                                              @RequestBody @Valid final LoginRequest loginRequest,
                                              final HttpServletRequest httpRequest) {
        final User user = this.userService.findActiveUserByIdentifier(loginRequest.phoneNumber())
                .orElseThrow(InvalidCredentialsException::new);

        // Verify password (pseudo-code)
        if (!this.userService.verifyPassword(user, loginRequest.password())) {
            this.userService.recordFailedLogin(loginRequest.phoneNumber(),
                    httpRequest.getRemoteAddr(), userAgent,
                    loginRequest.deviceId(), "Invalid password");

            throw new InvalidCredentialsException();
        }

        // Check if MFA is required
//            if (Boolean.TRUE.equals(user.getMfaEnabled())) {
//                return ResponseEntity.status(HttpStatus.OK)
//                        .body(Map.of("mfa_required", true, "user_id", user.getId()));
//            }

        // Create session and return tokens
        final AuthResponse response = this.userSessionService.createSession(user,
                loginRequest.deviceId(), loginRequest.deviceName(),
                loginRequest.deviceType(), httpRequest.getRemoteAddr(),
                userAgent, 24, 30);

        this.userService.updateLastLogin(user, httpRequest.getRemoteAddr(),
                userAgent, loginRequest.deviceId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") final String authHeader) {
        final String sessionToken = authHeader.replace("Bearer ", "");

        this.userSessionService.revokeSessionByToken(sessionToken);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        return this.userSessionService.refreshSession(refreshToken)
                .map(session -> ResponseEntity.ok(Map.of(
                        "session_token", session.getSessionToken(),
                        "expires_at", session.getExpiresAt()
                )))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token")));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody final PasswordResetRequest request) {
        this.passwordResetService.createPasswordResetToken(request.email());

        return ResponseEntity.ok(Map.of("message", "Password reset instructions sent"));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<Void> confirmPasswordReset(@RequestBody final PasswordResetRequest request) {
        final String token = request.token();
        final String newPassword = request.newPassword();

        this.passwordResetService.resetPassword(token, newPassword);

        return ResponseEntity.ok().build();
    }
}
