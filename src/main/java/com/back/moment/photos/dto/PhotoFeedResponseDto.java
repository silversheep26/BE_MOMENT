package com.back.moment.photos.dto;

import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PhotoFeedResponseDto {
    private Long photoId;
    private String photoUrl;
    private String nickName;
    private String profileImgUrl;
    private RoleEnum role;
    private int loveCnt;
    private String content;
    private List<String> tag_photoList;

    public PhotoFeedResponseDto(Photo photo) {
        this.photoId = photo.getId();
        this.photoUrl = photo.getImagUrl();
        this.nickName = photo.getUsers().getNickName();
        this.role = photo.getUsers().getRole();
        this.loveCnt = photo.getLoveCnt();
        this.profileImgUrl = photo.getUsers().getProfileImg();
        this.content = photo.getContents();
        this.tag_photoList = photo.getTagListWithWell();
    }
}
