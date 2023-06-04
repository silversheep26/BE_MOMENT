package com.back.moment.chat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;
    private String message;
    private Long senderId; // 채팅을 보내는 유저의 아이디
    private Long receiverId; // 채팅을 받는 유저의 아이디
    private Long chatRoomId;
    private LocalDateTime createdAt;
    private Boolean readStatus = false; // 채팅의 읽음 , 읽지않음 표시

    private Chat(String message, Long senderId, Long chatRoomId, LocalDateTime createdAt) {
        this.message = message;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
    }

    public static Chat of(String message, Long senderId, Long chatRoomId, LocalDateTime createdAt){
        return new Chat(message,senderId,chatRoomId,createdAt);
    }
}

