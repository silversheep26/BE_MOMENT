package com.back.moment.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoveCheckResponseDto {
    private boolean loveCheck;
    private int totalLoveCnt;

    public LoveCheckResponseDto(boolean loveCheck) {
        this.loveCheck = loveCheck;
    }
}
