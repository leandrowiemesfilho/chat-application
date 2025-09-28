package com.chatapp.message.model.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ChatDto(Long id,
                      String name,
                      Set<Long> participantIds,
                      Boolean isGroup,
                      MessageResponse lastMessage,
                      LocalDateTime createdAt) {
}
