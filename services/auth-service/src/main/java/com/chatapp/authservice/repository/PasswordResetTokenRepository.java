package com.chatapp.authservice.repository;

import com.chatapp.authservice.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(final String token);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.user.id = :userId AND t.used = false AND t.expiresAt > :currentTime")
    Optional<PasswordResetToken> findValidTokenByUserId(@Param("userId") final Long userId,
                                                        @Param("currentTime") final ZonedDateTime currentTime);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user.id = :userId AND t.used = false")
    void invalidateUserTokens(@Param("userId") final Long userId);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :currentTime")
    void deleteExpiredTokens(@Param("currentTime") final ZonedDateTime currentTime);
}
