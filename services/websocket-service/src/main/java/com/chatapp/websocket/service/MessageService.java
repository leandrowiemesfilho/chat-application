package com.chatapp.websocket.service;

import com.chatapp.websocket.model.ChatMessage;
import com.chatapp.websocket.model.DeliveryStatusUpdate;
import com.chatapp.websocket.model.enums.MessageDeliveryStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(final ChatMessage message) {
        // Send to specific user
        this.messagingTemplate.convertAndSendToUser(
                message.recipientId(),
                "/queue/messages",
                message
        );

        // Also send to sender for sync
        this.messagingTemplate.convertAndSendToUser(
                message.senderId(),
                "/queue/messages",
                message
        );
    }

    public void sendDeliveryStatus(final String messageId, final String recipientId,
                                   final MessageDeliveryStatus status) {
        final DeliveryStatusUpdate statusUpdate = new DeliveryStatusUpdate(messageId, status);

        this.messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/message-status",
                statusUpdate
        );
    }
}
