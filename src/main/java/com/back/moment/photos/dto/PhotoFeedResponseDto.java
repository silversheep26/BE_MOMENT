package com.back.moment.photos.dto;

import com.back.moment.photos.entity.Photo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoFeedResponseDto {
    private Long photoId;
    private String photoUrl;
    private String nickName;
    private int loveCnt;

    public PhotoFeedResponseDto(Photo photo) {
        this.photoId = photo.getId();
        this.photoUrl = photo.getImagUrl();
        this.nickName = photo.getUsers().getNickName();
        this.loveCnt = photo.getLoveCnt();
    }
}
