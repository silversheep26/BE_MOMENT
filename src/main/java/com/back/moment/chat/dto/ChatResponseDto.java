package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;

import java.time.LocalDateTime;

public class ChatResponseDto {
    private String message;
    private Long senderId;
    private Long receiverId;
    private Long roomId;
    private LocalDateTime createdAt;
    private boolean readStatus;
    private boolean userAcanSee;
    private boolean userBcanSee;

    private ChatResponseDto(String message, Long senderId, Long roomId, LocalDateTime createdAt,boolean readStatus,boolean userAcanSee,boolean userBcanSee) {
        this.message = message;
        this.senderId = senderId;
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.readStatus = readStatus;
        this.userAcanSee = userAcanSee;
        this.userBcanSee = userBcanSee;
    }

    public static ChatResponseDto from(Chat chat){
        return new ChatResponseDto(chat.getMessage(), chat.getSenderId(), chat.getChatRoomId(),chat.getCreatedAt(), chat.getReadStatus(),chat.getUserOneCanSee(), chat.getUserTwoCanSee());
    }
}