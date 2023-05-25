package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;

import java.time.LocalDateTime;

public class ChatResponseDto {
    private String message;
    private Long senderId;
    private Long roomId;
    private LocalDateTime createdAt;

    private ChatResponseDto(String message, Long senderId, Long roomId, LocalDateTime createdAt) {
        this.message = message;
        this.senderId = senderId;
        this.roomId = roomId;
        this.createdAt = createdAt;
    }

    public static ChatResponseDto from(Chat chat){
        return new ChatResponseDto(chat.getMsg(), chat.getSenderId(), chat.getRoomId(),chat.getCreatedAt());
    }
}
