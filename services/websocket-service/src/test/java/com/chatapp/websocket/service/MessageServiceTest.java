package com.chatapp.websocket.service;

import com.chatapp.websocket.model.ChatMessage;
import com.chatapp.websocket.model.enums.MessageDeliveryStatus;
import com.chatapp.websocket.model.enums.MessageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageService messageService;

    @Test
    void testSendMessage() {
        // Given
        final ChatMessage message = new ChatMessage(
                "userId",
                null,
                "user1",
                "user2",
                "Hello!",
                MessageType.TEXT,
                LocalDateTime.now(),
                MessageDeliveryStatus.SENT
        );

        // When
        this.messageService.sendMessage(message);

        // Then
        verify(this.messagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/messages"),
                eq(message)
        );

        verify(this.messagingTemplate).convertAndSendToUser(
                eq("user1"),
                eq("/queue/messages"),
                eq(message)
        );
    }

    @Test
    void testSendDeliveryStatus() {
        // Given
        String messageId = "msg123";
        String recipientId = "user1";
        final MessageDeliveryStatus status = MessageDeliveryStatus.DELIVERED;

        // When
        this.messageService.sendDeliveryStatus(messageId, recipientId, status);

        // Then
        verify(this.messagingTemplate).convertAndSendToUser(
                eq("user1"),
                eq("/queue/message-status"),
                org.mockito.ArgumentMatchers.any()
        );
    }
}
