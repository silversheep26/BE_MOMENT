package com.back.moment.feed.dto;

import com.back.moment.global.dto.TagResponseDto;
import com.back.moment.photos.entity.Photo;
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
public class FeedDetailResponseDto {
    private Long hostId;
    private String photoUrl;
    private int photoLoveCnt;
    private String profileUrl;
    private String nickName;
    private String contents;
    private String role;
    private boolean checkLove = false;
    private List<TagResponseDto> tag_photoList;
    private LocalDateTime createdTime;

    public FeedDetailResponseDto(Photo photo) {
        this.hostId = photo.getUsers().getId();
        this.photoUrl = photo.getImagUrl();
        this.photoLoveCnt = photo.getLoveCnt();
        this.profileUrl = photo.getUsers().getProfileImg();
        this.nickName = photo.getUsers().getNickName();
        this.role = photo.getUsers().getRole();
        this.contents = photo.getContents();
        this.tag_photoList = photo.getTagListWithWell();
        this.createdTime = photo.getCreatedAt().plusHours(9L);
    }
}
