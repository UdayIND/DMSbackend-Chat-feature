package com.dms.backend.service;

import com.dms.backend.model.ChatMessage;
import com.dms.backend.model.ChatRoom;
import com.dms.backend.repository.ChatMessageRepository;
import com.dms.backend.repository.ChatRoomRepository;
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

    public ChatService(
            ChatRoomRepository chatRoomRepository,
            ChatMessageRepository chatMessageRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
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
}
