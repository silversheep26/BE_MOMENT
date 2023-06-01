package com.back.moment.feed.dto;

import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;

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
//    private boolean checkRecommend;

    public FeedDetailResponseDto(Long id, String photoUrl, int loveCnt, String profileUrl, String nickName, RoleEnum role, String contents, boolean checkLove) {
        this.hostId = id;
        this.photoUrl = photoUrl;
        this.photoLoveCnt = loveCnt;
        this.profileUrl = profileUrl;
        this.nickName = nickName;
        this.role = role;
        this.contents = contents;
        this.checkLove = checkLove;
    }
}
