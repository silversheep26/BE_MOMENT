package com.back.moment.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoveCheckResponseDto {
    private boolean loveCheck;

    public LoveCheckResponseDto(boolean loveCheck) {
        this.loveCheck = loveCheck;
    }
}
