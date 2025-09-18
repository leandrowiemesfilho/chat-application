package com.chatapp.websocket.service;

import com.chatapp.websocket.model.PresenceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PresenceService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String USER_SESSIONS_KEY = "user:sessions:";
    private static final String ONLINE_USERS_KEY = "online:users";

    @Autowired
    public PresenceService(final RedisTemplate<String, String> redisTemplate,
                           final SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void userConnected(final String username, final String sessionId) {
        // Add session to user's sessions
        this.redisTemplate.opsForSet().add(USER_SESSIONS_KEY + username, sessionId);

        // If first session, mark user as online and notify
        final Long sessionCount = this.redisTemplate.opsForSet().size(USER_SESSIONS_KEY + username);
        if (sessionCount != null && sessionCount == 1) {
            this.redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
            this.notifyPresenceChange(username, true);
        }
    }

    public void userDisconnected(final String username, final String sessionId) {
        // Remove session from user's sessions
        this.redisTemplate.opsForSet().remove(USER_SESSIONS_KEY + username, sessionId);

        // If no more sessions, mark user as offline and notify
        final Long sessionCount = this.redisTemplate.opsForSet().size(USER_SESSIONS_KEY + username);
        if (sessionCount != null && sessionCount == 0) {
            this.redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
            this.notifyPresenceChange(username, false);
        }
    }

    public boolean isUserOnline(final String username) {
        return Boolean.TRUE.equals(this.redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, username));
    }

    public Set<String> getOnlineUsers() {
        return this.redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }

    private void notifyPresenceChange(final String username, final boolean online) {
        final PresenceEvent event = new PresenceEvent(username, online, System.currentTimeMillis());

        this.messagingTemplate.convertAndSend("/topic/presence", event);
    }

}
