package com.chatapp.message.repository;

import com.chatapp.message.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByParticipantIdsContainsAndIsGroupFalse(final Long userId);

    List<Chat> findByParticipantIdsContains(final Long userId);

    @Query("SELECT c FROM Chat c WHERE :userId MEMBER OF c.participantIds AND c.isGroup = true")
    List<Chat> findGroupChatsByUserId(@Param("userId") final Long userId);

    boolean existsByParticipantIdsContainsAndId(final Long userId, final Long chatId);

}
