package com.back.moment.chat.repository;

import com.back.moment.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat,String> {
    List<Chat> findByChatRoomIdAndCreatedAtAfter(Long ChatRoomId,LocalDateTime date);
    Optional<Chat> findTopByChatRoomIdAndCreatedAtAfterOrderByCreatedAtDesc(Long chatRoomId, LocalDateTime createdAt);
}
