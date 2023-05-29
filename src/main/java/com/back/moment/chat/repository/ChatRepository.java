package com.back.moment.chat.repository;

import com.back.moment.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat,String> {
    List<Chat> findByChatRoomIdAndUserOneCanSeeTrueOrderByCreatedAt(Long chatRoomId);
    List<Chat> findByChatRoomIdAndUserTwoCanSeeTrueOrderByCreatedAt(Long chatRoomId);
    boolean existsChatRoomIdAndUserOneCanSeeTrueAndReadStatusFalseAndReceiverId(Long chatRoomId,Long receiverId);
    boolean existsChatRoomIdAndUserTwoSeeTrueAndReadStatusFalseAndReceiverId(Long chatRoomId,Long receiverId);
    List<Chat> findByChatRoomId(Long chatRoomId);

}
