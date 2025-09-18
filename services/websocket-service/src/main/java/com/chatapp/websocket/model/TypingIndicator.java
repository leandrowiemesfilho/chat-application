package com.chatapp.websocket.model;

public record TypingIndicator(String chatId,
                              String userId,
                              boolean typing) {
}
