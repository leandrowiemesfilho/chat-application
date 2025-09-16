package com.chatapp.authservice.service;

import com.chatapp.authservice.exception.InvalidTokenException;
import com.chatapp.authservice.model.PasswordResetToken;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.repository.PasswordResetTokenRepository;
import com.chatapp.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionService userSessionService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.password.reset.token.expiration:3600000}") // 1 hour default
    private long tokenExpirationMs;

    @Autowired
    public PasswordResetService(final EmailService emailService,
                                final UserRepository userRepository,
                                final PasswordEncoder passwordEncoder,
                                final UserSessionService userSessionService,
                                final PasswordResetTokenRepository passwordResetTokenRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSessionService = userSessionService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Transactional
    public void createPasswordResetToken(String email) {
        final Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Don't reveal that email doesn't exist for security reasons
            return;
        }

        User user = userOpt.get();

        // Invalidate any existing tokens
        passwordResetTokenRepository.invalidateUserTokens(user.getId());

        // Create new token
        final String token = UUID.randomUUID().toString();
        final String tokenHash = passwordEncoder.encode(token);
        final ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(tokenExpirationMs / 1000);
        final PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(expiresAt);

        this.passwordResetTokenRepository.save(resetToken);

        // Send email (in production)
        this.emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(final String token, final String newPassword) {
        // Find valid token
        final Optional<PasswordResetToken> tokenOpt = this.passwordResetTokenRepository.findAll().stream()
                .filter(t -> !t.getUsed() && t.getExpiresAt().isAfter(ZonedDateTime.now()))
                .filter(t -> this.passwordEncoder.matches(token, t.getTokenHash()))
                .findFirst();

        if (tokenOpt.isEmpty()) {
            throw new InvalidTokenException();
        }

        final PasswordResetToken resetToken = tokenOpt.get();
        final User user = resetToken.getUser();

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        this.passwordResetTokenRepository.save(resetToken);

        // Invalidate all user sessions
        this.userSessionService.revokeAllUserSessions(user.getId());
    }

    @Transactional
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteExpiredTokens(ZonedDateTime.now());
    }
}
