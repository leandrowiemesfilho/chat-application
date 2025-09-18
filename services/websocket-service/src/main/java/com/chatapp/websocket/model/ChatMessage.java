package com.chatapp.websocket.model;

import com.chatapp.websocket.model.enums.MessageDeliveryStatus;
import com.chatapp.websocket.model.enums.MessageType;

import java.time.LocalDateTime;

public record ChatMessage(String id,
                          String chatId,
                          String senderId,
                          String recipientId,
                          String content,
                          MessageType messageType,
                          LocalDateTime timestamp,
                          MessageDeliveryStatus status) {
}
