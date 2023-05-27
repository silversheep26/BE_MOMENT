package com.back.moment.chat.repository;

import com.back.moment.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat,Long> {
    List<Chat> findByChatRoomIdAndUserOneCanSeeTrueOrderByCreatedAt(Long chatRoomId);
    List<Chat> findByChatRoomIdAndUserTwoCanSeeTrueOrderByCreatedAt(Long chatRoomId);
}
