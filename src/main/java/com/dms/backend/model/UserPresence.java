package com.dms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_presence")
public class UserPresence {
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "role")
    private String role;

    @Column(nullable = false)
    private boolean online = false;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "display_name")
    private String displayName;
} 