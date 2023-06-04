package com.back.moment.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomInfoResponseDto {
    private Long chatRoomId;
    private LocalDateTime lastChatTime;
    private ChatResponseDto lastChat;
    private String receiverProfileImg;
    private Long receiverId;
    private String receiverNickName;
}
