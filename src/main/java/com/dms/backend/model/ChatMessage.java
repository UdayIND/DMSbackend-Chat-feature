package com.dms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String content;

    private String attachmentUrl;
    private String attachmentType;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private boolean read = false;
    private boolean delivered = false;
}
