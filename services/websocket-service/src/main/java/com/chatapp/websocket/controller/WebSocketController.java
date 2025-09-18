package com.chatapp.websocket.controller;

import com.chatapp.websocket.model.ChatMessage;
import com.chatapp.websocket.model.TypingIndicator;
import com.chatapp.websocket.service.MessageService;
import com.chatapp.websocket.service.NotificationService;
import com.chatapp.websocket.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

@Controller
public class WebSocketController {
    private final MessageService messageService;
    private final PresenceService presenceService;
    private final NotificationService notificationService;

    @Autowired
    public WebSocketController(final MessageService messageService,
                               final PresenceService presenceService,
                               final NotificationService notificationService) {
        this.messageService = messageService;
        this.presenceService = presenceService;
        this.notificationService = notificationService;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(final ChatMessage message, final Principal principal) {
        // Verify the sender matches the authenticated user
        if (!principal.getName().equals(message.senderId())) {
            throw new SecurityException("Sender ID doesn't match authenticated user");
        }

        this.messageService.sendMessage(message);
    }

    @MessageMapping("/typing")
    public void handleTypingIndicator(final TypingIndicator indicator, final Principal principal) {
        if (!principal.getName().equals(indicator.userId())) {
            throw new SecurityException("User ID doesn't match authenticated user");
        }

        this.notificationService.sendTypingIndicator(indicator);
    }

    @MessageMapping("/presence")
    @SendToUser("/queue/presence")
    public Map<String, Boolean> checkPresence(String userId) {
        return Map.of("online", this.presenceService.isUserOnline(userId));
    }

    @MessageMapping("/online-users")
    @SendToUser("/queue/online-users")
    public Set<String> getOnlineUsers() {
        return this.presenceService.getOnlineUsers();
    }
}
