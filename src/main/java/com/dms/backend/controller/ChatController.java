package com.dms.backend.controller;

import com.dms.backend.model.ChatMessage;
import com.dms.backend.model.ChatRoom;
import com.dms.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Chat Room Operations
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoom) {
        return ResponseEntity.ok(chatService.createOrGetChatRoom(
            chatRoom.getType(),
            chatRoom.getParticipant1Id(),
            chatRoom.getParticipant2Id()
        ));
    }

    @GetMapping("/rooms/{userId}")
    public ResponseEntity<List<ChatRoom>> getUserRooms(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getUserRooms(userId));
    }

    @PostMapping("/rooms/{roomId}/close")
    public ResponseEntity<Void> closeChatRoom(@PathVariable Long roomId) {
        chatService.closeChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

    // Message Operations
    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessage message) {
        chatService.sendMessage(
            String.valueOf(message.getRoomId()),
            message.getSenderId(),
            message.getContent(),
            message.getAttachmentUrl(),
            message.getAttachmentType()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getChatHistory(Long.parseLong(roomId), page, size));
    }

    @PostMapping("/messages/{messageId}/delivered")
    public ResponseEntity<Void> markMessageDelivered(@PathVariable Long messageId) {
        chatService.markMessageDelivered(messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageRead(@PathVariable Long messageId) {
        chatService.markMessageRead(messageId);
        return ResponseEntity.ok().build();
    }

    // WebSocket Message Handlers
    @MessageMapping("/chat.send")
    public void handleWebSocketMessage(@Payload ChatMessage message) {
        chatService.sendMessage(
            String.valueOf(message.getRoomId()),
            message.getSenderId(),
            message.getContent(),
            message.getAttachmentUrl(),
            message.getAttachmentType()
        );
    }

    @MessageMapping("/chat.typing")
    public void handleTypingEvent(@Payload String event) {
        // Format: "userId:roomId:typing/stopped"
        String[] parts = event.split(":");
        messagingTemplate.convertAndSendToUser(
            parts[1],
            "/queue/typing",
            parts[0] + ":" + parts[2]
        );
    }

    // Presence Management
    @PostMapping("/presence/{userId}/online")
    public ResponseEntity<Void> markUserOnline(@PathVariable String userId) {
        chatService.markUserOnline(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/presence/{userId}/offline")
    public ResponseEntity<Void> markUserOffline(@PathVariable String userId) {
        chatService.markUserOffline(userId);
        return ResponseEntity.ok().build();
    }
}
