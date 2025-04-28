package com.dms.backend.repository;

import com.dms.backend.model.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPresenceRepository extends JpaRepository<UserPresence, String> {
} 