package com.back.moment.chat.repository;

import com.back.moment.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat,Long> {
    Page<Chat> findByRoomIdOrderByCreatedAt(Long chatRoomId, Pageable pageable);
}
