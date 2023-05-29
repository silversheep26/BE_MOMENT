package com.back.moment.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long chatRoomId;
    private List<ChatResponseDto> chatList;
    private String receiverProfileImg;
    private Long receiverId;
    private String receiverNickName;
    private boolean existReadStatusIsFalse;
}
