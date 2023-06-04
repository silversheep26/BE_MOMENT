package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class ChatResponseDto {
    private String id;
    private String message;
    private Long senderId;
    private Long receiverId;
    private Long chatRoomId;
    private LocalDateTime createdAt;
    private boolean readStatus;

    private ChatResponseDto(String id, String message, Long senderId, Long receiverId, Long chatRoomId, LocalDateTime createdAt, boolean readStatus) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
        this.readStatus = readStatus;
    }

    public static ChatResponseDto from(Chat chat){
        return new ChatResponseDto(chat.getId(),chat.getMessage(),chat.getSenderId(),chat.getReceiverId(),chat.getChatRoomId(),chat.getCreatedAt(),chat.getReadStatus());
    }
}