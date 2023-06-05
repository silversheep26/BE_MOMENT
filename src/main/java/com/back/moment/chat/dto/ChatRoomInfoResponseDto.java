package com.back.moment.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomInfoResponseDto {
    private Long chatRoomId;
    private LocalDateTime lastChatTime;
    private ChatResponseDto lastChat;
    private String receiverProfileImg;
    private Long receiverId;
    private String receiverNickName;
    private Boolean haveToRead; // 읽어야 할것이 있을때 true, 없으면 false
}
