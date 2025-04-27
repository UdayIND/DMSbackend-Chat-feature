package com.dms.backend.repository;

import com.dms.backend.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT r FROM ChatRoom r WHERE " +
           "((r.participant1Id = :p1 AND r.participant2Id = :p2) OR " +
           "(r.participant1Id = :p2 AND r.participant2Id = :p1)) AND " +
           "r.active = :active")
    Optional<ChatRoom> findByParticipantsAndActive(
            @Param("p1") String participant1Id,
            @Param("p2") String participant2Id,
            @Param("active") boolean active);
}
