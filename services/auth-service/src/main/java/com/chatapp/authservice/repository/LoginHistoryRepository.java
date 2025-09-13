package com.chatapp.authservice.repository;

import com.chatapp.authservice.model.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    Page<LoginHistory> findByUserIdOrderByCreatedAtDesc(final Long userId, final Pageable pageable);

    @Query("""
                    SELECT 
                        lh 
                    FROM LoginHistory lh 
                    WHERE lh.user.id = :userId 
                    AND lh.createdAt BETWEEN :startDate 
                    AND :endDate ORDER BY lh.createdAt DESC
            """)
    Page<LoginHistory> findByUserIdAndDateRange(
            @Param("userId") final Long userId,
            @Param("startDate") final ZonedDateTime startDate,
            @Param("endDate") final ZonedDateTime endDate,
            final Pageable pageable
    );

    @Query("""
                    SELECT 
                        COUNT(lh) 
                    FROM LoginHistory lh 
                    WHERE lh.user.id = :userId 
                    AND lh.attemptType = 'FAILURE' 
                    AND lh.createdAt > :since
            """)
    Long countFailedAttemptsSince(@Param("userId") final Long userId, @Param("since") final ZonedDateTime since);
}
