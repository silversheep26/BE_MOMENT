package com.back.moment.photos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoMyPageResponseDto {
    private String photoUrl;
    private int loveCnt;

    public PhotoMyPageResponseDto(String photoUrl, int loveCnt){
        this.photoUrl = photoUrl;
        this.loveCnt = loveCnt;
    }
}
