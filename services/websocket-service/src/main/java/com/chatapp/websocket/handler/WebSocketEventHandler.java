package com.chatapp.websocket.handler;

import com.chatapp.websocket.service.PresenceService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEventHandler {
    private final PresenceService presenceService;

    public WebSocketEventHandler(final PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @EventListener
    public void handleWebSocketConnect(final SessionConnectedEvent event) {
        final SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        final String username = Objects.requireNonNull(headers.getUser()).getName();
        final String sessionId = headers.getSessionId();

        this.presenceService.userConnected(username, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
        final SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        final String username = Objects.requireNonNull(headers.getUser()).getName();
        final String sessionId = headers.getSessionId();

        this.presenceService.userDisconnected(username, sessionId);
    }
}
