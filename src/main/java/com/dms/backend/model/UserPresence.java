package com.dms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class UserPresence {
    @Id
    private String userId;

    @Column(nullable = false)
    private boolean online = false;

    private LocalDateTime lastSeen;
} 