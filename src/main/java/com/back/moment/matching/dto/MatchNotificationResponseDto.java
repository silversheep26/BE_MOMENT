package com.back.moment.matching.dto;

import com.back.moment.matching.entity.MatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchNotificationResponseDto {
    private Long boardId;
    private Long userId;
    private String userNickName;
    private String userProfileImg;
    private MatchStatus matchStatus;

    public MatchNotificationResponseDto(Long boardId, Long userId, String userNickName, String userProfileImg, MatchStatus matchStatus) {
        this.boardId = boardId;
        this.userId = userId;
        this.userNickName = userNickName;
        this.userProfileImg = userProfileImg;
        this.matchStatus = matchStatus;
    }
}
