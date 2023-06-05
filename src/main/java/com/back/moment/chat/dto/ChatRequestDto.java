package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRequestDto {
    private String message;
    private Long senderId;
    private Long receiverId;
    private Long chatRoomId;



    public static Chat toEntity(ChatRequestDto chatDto, LocalDateTime createdAt){
        return Chat.of(chatDto.getMessage(), chatDto.getSenderId(), chatDto.getReceiverId() , chatDto.getChatRoomId(),createdAt);
    }
    public void setChatRoomId(Long chatRoomId){
        this.chatRoomId = chatRoomId;
    }
}