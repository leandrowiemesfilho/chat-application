package com.chatapp.message.service;

import com.chatapp.message.model.Chat;
import com.chatapp.message.model.dto.ChatDto;

import java.util.List;

public interface ChatService {
    Chat getOrCreatePrivateChat(final Long user1Id, final Long user2Id);
    Chat createGroupChat(final String name, final List<Long> participantIds);
    List<ChatDto> getUserChats(final Long userId);
    void addParticipantToGroup(final Long chatId, final Long userId);
    void removeParticipantFromGroup(final Long chatId, final Long userId);
    ChatDto getChatById(final Long chatId, final Long userId);
}
