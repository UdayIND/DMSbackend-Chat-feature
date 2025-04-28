package com.dms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages", schema = "messaging")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_role")
    private String senderRole;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "delivered")
    private boolean delivered = false;

    @Column(name = "read")
    private boolean read = false;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // Convenience method to get roomId as String
    public String getRoomId() {
        return room != null ? String.valueOf(room.getId()) : null;
    }
}
