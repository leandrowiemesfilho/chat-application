package com.chatapp.authservice.service;

import com.chatapp.authservice.model.LoginHistory;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.repository.LoginHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;

    @Autowired
    public LoginHistoryService(final LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Transactional
    public void recordLogin(User user, String attemptType, String ipAddress,
                            String userAgent, String deviceId, String failureReason) {
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        history.setAttemptType(attemptType);
        history.setIpAddress(ipAddress);
        history.setUserAgent(userAgent);
        history.setDeviceId(deviceId);
        history.setFailureReason(failureReason);

        loginHistoryRepository.save(history);
    }

    public Page<LoginHistory> getUserLoginHistory(final Long userId, final Pageable pageable) {
        return this.loginHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<LoginHistory> getUserLoginHistoryByDateRange(final Long userId, final ZonedDateTime startDate,
                                                             final ZonedDateTime endDate, final Pageable pageable) {
        return this.loginHistoryRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable);
    }

    public Long getRecentFailedAttemptsCount(final Long userId, final ZonedDateTime since) {
        return this.loginHistoryRepository.countFailedAttemptsSince(userId, since);
    }
}
