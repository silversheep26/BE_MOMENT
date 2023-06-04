package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRequestDto {
    private String message;
    private Long senderId;
    private Long receiverId;
    private Long chatRoomId;


    public static Chat toEntity(ChatRequestDto chatDto, LocalDateTime createdAt){
        return Chat.of(chatDto.getMessage(), chatDto.getSenderId(),chatDto.getChatRoomId(),createdAt);
    }
    public void setChatRoomId(Long chatRoomId){
        this.chatRoomId = chatRoomId;
    }
}