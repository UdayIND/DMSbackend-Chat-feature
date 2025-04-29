package com.dms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_rooms", schema = "messaging")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(name = "participant1_id", nullable = false)
    private String participant1Id;

    @Column(name = "participant2_id", nullable = false)
    private String participant2Id;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "participants")
    private String participants;
}
