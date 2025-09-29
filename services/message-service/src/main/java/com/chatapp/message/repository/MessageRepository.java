package com.chatapp.message.repository;

import com.chatapp.message.model.Message;
import com.chatapp.message.model.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChatIdOrderByCreatedAtDesc(final Long chatId, final Pageable pageable);

    Page<Message> findByGroupIdOrderByCreatedAtDesc(final Long groupId, final Pageable pageable);

    List<Message> findByChatIdAndStatus(final Long chatId, final MessageStatus status);

    List<Message> findByGroupIdAndStatus(final Long groupId, final MessageStatus status);

    @Query("SELECT m FROM Message m WHERE m.chatId = :chatId AND m.id < :beforeMessageId ORDER BY m.createdAt DESC")
    Page<Message> findEarlierMessages(@Param("chatId") final Long chatId,
                                      @Param("beforeMessageId") final Long beforeMessageId,
                                      final Pageable pageable);

    long countByChatIdAndSenderIdNotAndStatus(final Long chatId, final Long senderId, final MessageStatus status);

}
