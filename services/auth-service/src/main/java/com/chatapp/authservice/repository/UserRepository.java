package com.chatapp.authservice.repository;

import com.chatapp.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(final String phoneNumber);

    Optional<User> findByEmail(final String email);

    boolean existsByPhoneNumber(final String phoneNumber);

    boolean existsByEmail(final String email);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND (u.phoneNumber = :identifier OR u.email = :identifier)")
    Optional<User> findActiveUserByIdentifier(@Param("identifier") String identifier);
}
