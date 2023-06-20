package com.back.moment.chat.dto;

import com.back.moment.chat.entity.Chat;
import com.back.moment.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
public class ChatDetailResponseDto {
    private String receiverNickName;
    private String receiverProfileImg;
    private String message;
    private LocalDateTime createdAt;
    private Long chatRoomId;

    public static ChatDetailResponseDto from(ChatResponseDto chat, Users users){
        return new ChatDetailResponseDto(users.getNickName(),
                                        users.getProfileImg(),
                                        chat.getMessage(),
                                        chat.getCreatedAt(),
                                        chat.getChatRoomId());
    }
}
