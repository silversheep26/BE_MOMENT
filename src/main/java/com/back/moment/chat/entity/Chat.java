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
    private String msg;
    private Long senderId;
    private Long roomId;
    private LocalDateTime createdAt;

    private Chat(String msg, Long senderId, Long roomId, LocalDateTime createdAt) {
        this.msg = msg;
        this.senderId = senderId;
        this.roomId = roomId;
        this.createdAt = createdAt;
    }

    public static Chat of(String msg, Long senderId, Long roomId, LocalDateTime createdAt){
        return new Chat(msg,senderId,roomId,createdAt);
    }
}

