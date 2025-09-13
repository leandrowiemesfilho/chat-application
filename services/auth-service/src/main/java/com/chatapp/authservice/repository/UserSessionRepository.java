package com.chatapp.authservice.repository;

import com.chatapp.authservice.model.UserSession;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, Long> {
    Optional<UserSession> findBySessionToken(final String token);

    Optional<UserSession> findByRefreshToken(final String refreshToken);

    List<UserSession> findByUserIdAndRevokedFalse(final Long id);

    @Query("SELECT s FROM UserSession s WHERE s.user.id = :userId AND s.deviceId = :deviceId AND s.revoked = false")
    Optional<UserSession> findActiveSessionByDevice(@Param("userId") final Long userId,
                                                    @Param("deviceId") final String deviceId);

    @Modifying
    @Query("UPDATE UserSession s SET s.revoked = true WHERE s.user.id = :userId AND s.revoked = false")
    void revokeAllUserSessions(@Param("userId") final Long userId);

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :currentTime")
    void deleteExpiredSessions(@Param("currentTime") final ZonedDateTime currentTime);
}
