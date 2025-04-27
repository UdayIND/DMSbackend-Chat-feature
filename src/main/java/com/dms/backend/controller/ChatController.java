package com.dms.backend.controller;

import com.dms.backend.model.ChatMessage;
import com.dms.backend.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.dms.backend.model.ChatMessage;
import com.dms.backend.model.ChatRoom;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest message, SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getUser().getName();
        chatService.sendMessage(
            message.getRoomId(),
            userId,
            message.getContent(),
            message.getAttachmentUrl(),
            message.getAttachmentType()
        );
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessage>> getChatHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId, page, size));
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(chatService.createOrGetChatRoom(
            request.getType(),
            request.getParticipant1Id(),
            request.getParticipant2Id()
        ));
    }

    @PostMapping("/rooms/{roomId}/close")
    public ResponseEntity<?> closeChatRoom(@PathVariable String roomId) {
        chatService.closeChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<ChatRoom>> getUserRooms(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getUserRooms(userId));
    }
}

class ChatMessageRequest {
    private String roomId;
    private String content;
    private String attachmentUrl;
    private String attachmentType;

    // Getters
    public String getRoomId() { return roomId; }
    public String getContent() { return content; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public String getAttachmentType() { return attachmentType; }
}

class CreateRoomRequest {
    private String type;
    private String participant1Id;
    private String participant2Id;

    // Getters
    public String getType() { return type; }
    public String getParticipant1Id() { return participant1Id; }
    public String getParticipant2Id() { return participant2Id; }
}
