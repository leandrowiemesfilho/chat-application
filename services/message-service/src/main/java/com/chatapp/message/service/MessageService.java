package com.chatapp.message.service;

import com.chatapp.message.model.dto.MessageRequest;
import com.chatapp.message.model.dto.MessageResponse;
import com.chatapp.message.model.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    MessageResponse sendMessage(final MessageRequest messageRequest, final String authHeader);
    Page<MessageResponse> getMessagesByChat(final Long chatId, final Pageable pageable, final String authHeader);
    Page<MessageResponse> getMessagesByGroup(final Long groupId, final Pageable pageable, final String authHeader);
    void updateMessageStatus(final Long messageId, final MessageStatus status, final String authHeader);
    MessageResponse getMessageById(final Long messageId, final String authHeader);
    void markMessagesAsRead(final Long chatId, final String authHeader);
    long getUnreadMessageCount(final Long chatId, final String authHeader);
}
