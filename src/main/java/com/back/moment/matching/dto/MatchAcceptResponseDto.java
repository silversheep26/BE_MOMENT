package com.back.moment.matching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchAcceptResponseDto {
    private String applyUserNickName;
    private String hostUserNickName;

    public MatchAcceptResponseDto(String applyUserNickName, String hostUserNickName) {
        this.applyUserNickName = applyUserNickName;
        this.hostUserNickName = hostUserNickName;
    }
}
