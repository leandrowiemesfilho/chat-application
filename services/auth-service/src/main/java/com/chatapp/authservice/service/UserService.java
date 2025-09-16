package com.chatapp.authservice.service;

import com.chatapp.authservice.exception.UserAlreadyExistsException;
import com.chatapp.authservice.mapper.UserMapper;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.model.enums.UserStatus;
import com.chatapp.authservice.model.dto.AuthResponse;
import com.chatapp.authservice.model.dto.RegisterRequest;
import com.chatapp.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginHistoryService loginHistoryService;

    @Autowired
    public UserService(final UserMapper userMapper,
                       final UserRepository userRepository,
                       final PasswordEncoder passwordEncoder,
                       final LoginHistoryService loginHistoryService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginHistoryService = loginHistoryService;
    }

    public Optional<User> findByPhoneNumber(final String phoneNumber) {
        return this.userRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<User> findByEmail(final String email) {
        return this.userRepository.findByEmail(email);
    }

    public Optional<User> findActiveUserByIdentifier(final String identifier) {
        return this.userRepository.findActiveUserByIdentifier(identifier);
    }

    public boolean existsByPhoneNumber(final String phoneNumber) {
        return this.userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean existsByEmail(final String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean verifyPassword(final User user, final String rawPassword) {
        return this.passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    @Transactional
    public AuthResponse registerUser(final RegisterRequest registerRequest) {
        final String phoneNumber = registerRequest.phoneNumber();

        if (this.existsByPhoneNumber(phoneNumber)) {
            throw new UserAlreadyExistsException();
        }

        if (registerRequest.email() != null && !registerRequest.email().isEmpty() &&
                this.existsByEmail(registerRequest.email())) {
            throw new UserAlreadyExistsException();
        }

        final User user = this.userMapper.fromRegisterRequest(registerRequest);

        this.userRepository.save(user);

        return this.userMapper.toAuthResponseWithToken(user);
    }

    @Transactional
    public void updateLastLogin(final User user, final String ipAddress,
                                final String userAgent, final String deviceId) {
        user.setLastLogin(ZonedDateTime.now());

        this.userRepository.save(user);

        // Record successful login
        this.loginHistoryService.recordLogin(user, "SUCCESS", ipAddress, userAgent, deviceId, null);
    }

    @Transactional
    public void recordFailedLogin(final String identifier, final String ipAddress, final String userAgent,
                                  final String deviceId, final String failureReason) {
        this.findActiveUserByIdentifier(identifier).ifPresent(user -> {

            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            user.setLastFailedLogin(ZonedDateTime.now());

            this.userRepository.save(user);

            // Record failed login
            this.loginHistoryService.recordLogin(user, "FAILURE", ipAddress, userAgent, deviceId, failureReason);
        });
    }

    @Transactional
    public void resetFailedLoginAttempts(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFailedLoginAttempts(0);

        this.userRepository.save(user);
    }

    @Transactional
    public void updatePublicKey(final String phoneNumber, final String publicKey) {
        final User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPublicKey(publicKey);

        this.userRepository.save(user);
    }

    @Transactional
    public void updateUserStatus(final Long userId, final UserStatus status) {
        final User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(status);

        this.userRepository.save(user);
    }

    @Transactional
    public void enableMfa(Long userId, String mfaSecret, String[] recoveryCodes) {
        final User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setMfaEnabled(true);
        user.setMfaSecret(mfaSecret);
        user.setRecoveryCodes(recoveryCodes);

        this.userRepository.save(user);
    }

    @Transactional
    public void disableMfa(final Long userId) {
        final User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        user.setRecoveryCodes(null);

        this.userRepository.save(user);
    }
}
