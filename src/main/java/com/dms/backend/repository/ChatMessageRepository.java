package com.dms.backend.repository;

import com.dms.backend.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
    
    @Query("SELECT DISTINCT m.roomId FROM ChatMessage m WHERE m.senderId = :userId OR m.roomId LIKE CONCAT('%', :userId, '%')")
    List<String> findDistinctRoomIdsByUserId(@Param("userId") String userId);
    
    List<ChatMessage> findByRoomIdAndReadFalseAndSenderIdNot(String roomId, String userId);
}
