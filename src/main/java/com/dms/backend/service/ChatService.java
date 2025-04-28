package com.dms.backend.service;

import com.dms.backend.model.ChatMessage;
import com.dms.backend.model.ChatRoom;
import com.dms.backend.model.UserPresence;
import com.dms.backend.repository.ChatMessageRepository;
import com.dms.backend.repository.ChatRoomRepository;
import com.dms.backend.repository.UserPresenceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserPresenceRepository userPresenceRepository;

    public ChatService(
            ChatRoomRepository chatRoomRepository,
            ChatMessageRepository chatMessageRepository,
            SimpMessagingTemplate messagingTemplate,
            UserPresenceRepository userPresenceRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
        this.userPresenceRepository = userPresenceRepository;
    }

    @Transactional
    public ChatRoom createOrGetChatRoom(String type, String participant1Id, String participant2Id) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByParticipantsAndActive(
                participant1Id, participant2Id, true);
        
        return existingRoom.orElseGet(() -> {
            ChatRoom newRoom = new ChatRoom();
            newRoom.setType(type);
            newRoom.setParticipant1Id(participant1Id);
            newRoom.setParticipant2Id(participant2Id);
            return chatRoomRepository.save(newRoom);
        });
    }

    @Transactional
    public void sendMessage(String roomId, String senderId, String content, String attachmentUrl, String attachmentType) {
        ChatRoom room = chatRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setAttachmentUrl(attachmentUrl);
        message.setAttachmentType(attachmentType);

        chatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    public Page<ChatMessage> getChatHistory(String roomId, int page, int size) {
        return chatMessageRepository.findByRoomId(
                Long.parseLong(roomId),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
    }

    @Transactional
    public void closeChatRoom(String roomId) {
        ChatRoom room = chatRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        room.setActive(false);
        room.setClosedAt(LocalDateTime.now());
        chatRoomRepository.save(room);
    }

    public List<ChatRoom> getUserRooms(String userId) {
        return chatRoomRepository.findActiveRoomsByUserId(userId);
    }

    // Group chat creation
    @Transactional
    public ChatRoom createOrGetGroupChatRoom(String type, List<String> participantIds) {
        String participantsStr = String.join(",", participantIds);
        // Find existing group chat by type and participants
        List<ChatRoom> existingRooms = chatRoomRepository.findAll();
        for (ChatRoom room : existingRooms) {
            if (room.getType().equals(type) && participantsStr.equals(room.getParticipants())) {
                return room;
            }
        }
        ChatRoom newRoom = new ChatRoom();
        newRoom.setType(type);
        newRoom.setParticipants(participantsStr);
        newRoom.setActive(true);
        return chatRoomRepository.save(newRoom);
    }

    // Presence
    public void setUserOnline(String userId) {
        UserPresence presence = userPresenceRepository.findById(userId).orElse(new UserPresence());
        presence.setUserId(userId);
        presence.setOnline(true);
        presence.setLastSeen(LocalDateTime.now());
        userPresenceRepository.save(presence);
    }
    public void setUserOffline(String userId) {
        UserPresence presence = userPresenceRepository.findById(userId).orElse(new UserPresence());
        presence.setUserId(userId);
        presence.setOnline(false);
        presence.setLastSeen(LocalDateTime.now());
        userPresenceRepository.save(presence);
    }
    public boolean isUserOnline(String userId) {
        return userPresenceRepository.findById(userId).map(UserPresence::isOnline).orElse(false);
    }

    // Message status
    @Transactional
    public void markMessageDelivered(Long messageId) {
        chatMessageRepository.findById(messageId).ifPresent(msg -> {
            msg.setDelivered(true);
            chatMessageRepository.save(msg);
        });
    }
    @Transactional
    public void markMessageRead(Long messageId) {
        chatMessageRepository.findById(messageId).ifPresent(msg -> {
            msg.setRead(true);
            chatMessageRepository.save(msg);
        });
    }

    // Typing status (simple broadcast, not persisted)
    public void sendTypingStatus(String roomId, String userId, boolean typing) {
        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/typing", userId + (typing ? ":typing" : ":stopped"));
    }
}
