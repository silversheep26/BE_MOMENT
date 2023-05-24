package com.back.moment.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedDetailResponseDto {
    private String photoUrl;
    private String profileUrl;
    private String nickName;
    private boolean checkLove;
    private boolean checkRecommend;

    public FeedDetailResponseDto(String photoUrl, String profileUrl, String nickName) {
        this.photoUrl = photoUrl;
        this.profileUrl = profileUrl;
        this.nickName = nickName;
    }
}
