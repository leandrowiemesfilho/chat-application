package com.chatapp.message.model.dto;

import com.chatapp.message.model.enums.MessageStatus;
import com.chatapp.message.model.enums.MessageType;

import java.time.LocalDateTime;
import java.util.Set;

public record MessageResponse(Long id,
                              Long senderId,
                              Long chatId,
                              Long groupId,
                              MessageType messageType,
                              String content,
                              MessageStatus status,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Set<Long> readBy) {
}
