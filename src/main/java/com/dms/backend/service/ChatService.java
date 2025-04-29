package com.dms.backend.service;

import com.dms.backend.model.ChatMessage;
import com.dms.backend.model.ChatRoom;
import com.dms.backend.repository.ChatMessageRepository;
import com.dms.backend.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ConcurrentHashMap<String, Boolean> userPresence = new ConcurrentHashMap<>();

    // Chat Room Operations
    public ChatRoom createOrGetChatRoom(String type, String participant1Id, String participant2Id) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByParticipantsAndActive(
                participant1Id, participant2Id, true);
        
        return existingRoom.orElseGet(() -> {
            ChatRoom newRoom = new ChatRoom();
            newRoom.setType(type);
            newRoom.setParticipant1Id(participant1Id);
            newRoom.setParticipant2Id(participant2Id);
            newRoom.setActive(true);
            newRoom.setCreatedAt(LocalDateTime.now());
            return chatRoomRepository.save(newRoom);
        });
    }

    public List<ChatRoom> getUserRooms(String userId) {
        return chatRoomRepository.findActiveRoomsByUserId(userId);
    }

    public void closeChatRoom(Long roomId) {
        chatRoomRepository.findById(roomId).ifPresent(room -> {
            room.setActive(false);
            room.setClosedAt(LocalDateTime.now());
            chatRoomRepository.save(room);
        });
    }

    // Message Operations
    public void sendMessage(String roomId, String senderId, String content, String attachmentUrl, String attachmentType) {
        ChatRoom room = chatRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        ChatMessage message = new ChatMessage();
        message.setRoomId(room.getId());
        message.setSenderId(senderId);
        message.setContent(content);
        message.setAttachmentUrl(attachmentUrl);
        message.setAttachmentType(attachmentType);
        message.setCreatedAt(LocalDateTime.now());
        message.setDelivered(false);
        message.setRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    public List<ChatMessage> getChatHistory(Long roomId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatMessage> messagePage = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageRequest);
        return messagePage.getContent();
    }

    public void markMessageDelivered(Long messageId) {
        chatMessageRepository.findById(messageId).ifPresent(message -> {
            message.setDelivered(true);
            chatMessageRepository.save(message);
        });
    }

    public void markMessageRead(Long messageId) {
        chatMessageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            chatMessageRepository.save(message);
        });
    }

    // Presence Management
    public void markUserOnline(String userId) {
        userPresence.put(userId, true);
        messagingTemplate.convertAndSend("/topic/presence", userId + ":online");
    }

    public void markUserOffline(String userId) {
        userPresence.put(userId, false);
        messagingTemplate.convertAndSend("/topic/presence", userId + ":offline");
    }

    public boolean isUserOnline(String userId) {
        return userPresence.getOrDefault(userId, false);
    }
}
