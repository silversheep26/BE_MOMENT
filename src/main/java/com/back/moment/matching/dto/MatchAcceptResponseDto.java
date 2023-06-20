package com.back.moment.matching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchAcceptResponseDto {
    private Long boardId;
    private String applyUserNickName;
    private String hostUserNickName;

    public MatchAcceptResponseDto(Long boardId, String applyUserNickName, String hostUserNickName) {
        this.boardId = boardId;
        this.applyUserNickName = applyUserNickName;
        this.hostUserNickName = hostUserNickName;
    }
}
