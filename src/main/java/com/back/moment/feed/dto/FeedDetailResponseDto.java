package com.back.moment.feed.dto;

import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeedDetailResponseDto {
    private Long hostId;
    private String photoUrl;
    private int photoLoveCnt;
    private String profileUrl;
    private String nickName;
    private String contents;
    private RoleEnum role;
    private boolean checkLove = false;
    private List<String> tag_photoList;

    public FeedDetailResponseDto(Photo photo) {
        this.hostId = photo.getUsers().getId();
        this.photoUrl = photo.getImagUrl();
        this.photoLoveCnt = photo.getLoveCnt();
        this.profileUrl = photo.getUsers().getProfileImg();
        this.nickName = photo.getUsers().getNickName();
        this.role = photo.getUsers().getRole();
        this.contents = photo.getContents();
        this.tag_photoList = photo.getTagListWithWell();
    }
}
