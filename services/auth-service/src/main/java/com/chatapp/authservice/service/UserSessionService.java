package com.chatapp.authservice.service;

import com.chatapp.authservice.mapper.UserMapper;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.model.UserSession;
import com.chatapp.authservice.model.dto.AuthResponse;
import com.chatapp.authservice.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserSessionService {
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public UserSessionService(final UserMapper userMapper,
                              final JwtService jwtService,
                              final UserSessionRepository userSessionRepository) {
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.userSessionRepository = userSessionRepository;
    }

    @Transactional
    public AuthResponse createSession(final User user, final String deviceId, final String deviceName,
                                      final String deviceType, final String ipAddress, final String userAgent,
                                      final int sessionDurationHours, final int refreshDurationDays) {

        // Check for existing session on the same device
        final Optional<UserSession> existingSession = this.userSessionRepository
                .findActiveSessionByDevice(user.getId(), deviceId);

        existingSession.ifPresent(userSession -> this.revokeSession(userSession.getId()));

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(sessionDurationHours);
        final ZonedDateTime refreshExpiresAt = now.plusDays(refreshDurationDays);
        final String sessionToken = this.jwtService.generateToken(user);
        final UserSession session = new UserSession();

        session.setUser(user);
        session.setSessionToken(sessionToken);
        session.setRefreshToken(generateToken());
        session.setDeviceId(deviceId);
        session.setDeviceName(deviceName);
        session.setDeviceType(deviceType);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(expiresAt);

        this.userSessionRepository.save(session);

        return this.userMapper.toAuthResponseWithToken(
                session.getSessionToken(),
                session.getRefreshToken(),
                refreshExpiresAt,
                user
        );
    }

    public Optional<UserSession> validateSession(final String sessionToken) {
        return this.userSessionRepository.findBySessionToken(sessionToken)
                .filter(session -> !session.getRevoked())
                .filter(session -> session.getExpiresAt().isAfter(ZonedDateTime.now()));
    }

    @Transactional
    public Optional<UserSession> refreshSession(final String refreshToken) {
        return this.userSessionRepository.findByRefreshToken(refreshToken)
                .filter(session -> !session.getRevoked())
                .map(session -> {
                    session.setSessionToken(generateToken());
                    session.setExpiresAt(ZonedDateTime.now().plusHours(24)); // Refresh for 24 hours

                    return this.userSessionRepository.save(session);
                });
    }

    @Transactional
    public void revokeSession(final Long sessionId) {
        this.userSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setRevoked(true);

            this.userSessionRepository.save(session);
        });
    }

    @Transactional
    public void revokeSessionByToken(final String sessionToken) {
        this.userSessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            session.setRevoked(true);

            this.userSessionRepository.save(session);
        });
    }

    @Transactional
    public void revokeAllUserSessions(final Long userId) {
        this.userSessionRepository.revokeAllUserSessions(userId);
    }

    public List<UserSession> getUserSessions(final Long userId) {
        return this.userSessionRepository.findByUserIdAndRevokedFalse(userId);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void cleanupExpiredSessions() {
        this.userSessionRepository.deleteExpiredSessions(ZonedDateTime.now());
    }

    private String generateToken() {
        return UUID.randomUUID() + "-" + System.currentTimeMillis();
    }

    public boolean isSessionValid(final String sessionToken) {
        return this.validateSession(sessionToken).isPresent();
    }

    public Optional<User> getUserFromSession(final String sessionToken) {
        return this.validateSession(sessionToken).map(UserSession::getUser);
    }
}
