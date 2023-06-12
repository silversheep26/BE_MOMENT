package com.back.moment.photos.dto;

import com.back.moment.global.dto.TagResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PhotoFeedResponseDto {
    private Long photoId;
    private Long hostId;
    private String photoUrl;
    private String nickName;
    private String profileImgUrl;
    private RoleEnum role;
    private int loveCnt;
    private String content;
    private List<TagResponseDto> tag_photoList;
    private boolean loveCheck;
    private LocalDateTime createdTime;

    public PhotoFeedResponseDto(Photo photo, boolean loveCheck) {
        this.photoId = photo.getId();
        this.hostId = photo.getUsers().getId();
        this.photoUrl = photo.getImagUrl();
        this.nickName = photo.getUsers().getNickName();
        this.role = photo.getUsers().getRole();
        this.loveCnt = photo.getLoveCnt();
        this.profileImgUrl = photo.getUsers().getProfileImg();
        this.content = photo.getContents();
        this.tag_photoList = photo.getTagListWithWell();
        this.loveCheck = loveCheck;
        this.createdTime = photo.getCreatedAt().plusHours(9L);
    }
}
