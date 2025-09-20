package com.chatapp.websocket.service;

import com.chatapp.websocket.model.PresenceEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private PresenceService presenceService;

    @Test
    void testUserConnectedFirstSession() {
        // Given
        when(this.redisTemplate.opsForSet()).thenReturn(setOperations);
        when(this.setOperations.size(anyString())).thenReturn(0L);

        // When
        this.presenceService.userConnected("user1", "session123");

        // Then
        verify(this.setOperations).add(eq("user:sessions:user1"), eq("session123"));
        verify(this.setOperations).add(eq("online:users"), eq("user1"));
        verify(this.messagingTemplate).convertAndSend(eq("/topic/presence"), any(PresenceEvent.class));
    }

    @Test
    void testUserConnectedAdditionalSession() {
        // Given
        when(this.redisTemplate.opsForSet()).thenReturn(setOperations);
        when(this.setOperations.size(anyString())).thenReturn(1L);

        // When
        this.presenceService.userConnected("user1", "session456");

        // Then
        verify(this.setOperations).add(eq("user:sessions:user1"), eq("session456"));
        verify(this.setOperations, never()).add(eq("online:users"), eq("user1"));
        verify(this.messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }
}