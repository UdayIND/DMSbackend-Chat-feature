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
import java.util.Arrays;

import com.dms.backend.model.ChatRoom;
import com.dms.backend.model.UserPresence;
import org.springframework.http.HttpStatus;

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

    // Group chat creation
    @PostMapping("/rooms/group")
    public ResponseEntity<?> createGroupChatRoom(@RequestBody GroupChatRoomRequest request) {
        return ResponseEntity.ok(chatService.createOrGetGroupChatRoom(
            request.getType(),
            request.getParticipantIdsList()
        ));
    }

    // Presence endpoints
    @PostMapping("/presence/{userId}/online")
    public ResponseEntity<?> setUserOnline(@PathVariable String userId) {
        chatService.setUserOnline(userId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/presence/{userId}/offline")
    public ResponseEntity<?> setUserOffline(@PathVariable String userId) {
        chatService.setUserOffline(userId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/presence/{userId}")
    public ResponseEntity<UserPresence> getUserPresence(@PathVariable String userId) {
        return chatService.isUserOnline(userId)
            ? ResponseEntity.ok().body(new UserPresence())
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Message status endpoints
    @PostMapping("/messages/{messageId}/delivered")
    public ResponseEntity<?> markMessageDelivered(@PathVariable Long messageId) {
        chatService.markMessageDelivered(messageId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<?> markMessageRead(@PathVariable Long messageId) {
        chatService.markMessageRead(messageId);
        return ResponseEntity.ok().build();
    }

    // Typing status endpoint
    @PostMapping("/rooms/{roomId}/typing")
    public ResponseEntity<?> sendTypingStatus(@PathVariable String roomId, @RequestParam String userId, @RequestParam boolean typing) {
        chatService.sendTypingStatus(roomId, userId, typing);
        return ResponseEntity.ok().build();
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

class GroupChatRoomRequest {
    private String type;
    private String[] participantIds;
    public String getType() { return type; }
    public String[] getParticipantIds() { return participantIds; }
    public java.util.List<String> getParticipantIdsList() { return Arrays.asList(participantIds); }
}
