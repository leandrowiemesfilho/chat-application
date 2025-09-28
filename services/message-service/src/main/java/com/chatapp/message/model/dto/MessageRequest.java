package com.chatapp.message.model.dto;

import com.chatapp.message.model.enums.MessageType;

public record MessageRequest(Long senderId,
                             Long chatId,
                             Long groupId,
                             MessageType messageType,
                             String content,
                             String replyToMessageId) {
}
