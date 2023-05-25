package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRequestDto {
    private String message;
    private Long senderId;
    private Long roomId;


    public static Chat toEntity(ChatRequestDto chatDto, LocalDateTime createdAt){
        return Chat.of(chatDto.getMessage(), chatDto.getSenderId(),chatDto.getRoomId(),createdAt);
    }

}