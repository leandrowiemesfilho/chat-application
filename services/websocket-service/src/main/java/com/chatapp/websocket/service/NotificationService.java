package com.chatapp.websocket.service;

import com.chatapp.websocket.model.TypingIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendTypingIndicator(final TypingIndicator indicator) {
        this.messagingTemplate.convertAndSendToUser(
                indicator.userId(),
                "/queue/typing",
                indicator
        );
    }

    public void sendNotification(final String userId, final String message) {
        this.messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                Map.of("message", message, "timestamp", System.currentTimeMillis())
        );
    }

}
