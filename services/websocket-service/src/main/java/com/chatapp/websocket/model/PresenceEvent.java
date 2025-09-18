package com.chatapp.websocket.model;

public record PresenceEvent(String userId,
                            boolean online,
                            long lastSeen) {
}
