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
    private Long id;
    private String photoUrl;
    private String profileUrl;
    private String nickName;
    private String contents;
    private RoleEnum role;
    private boolean checkLove;
    private boolean checkRecommend;

    public FeedDetailResponseDto(Long id, String photoUrl, String profileUrl, String nickName, RoleEnum role, String contents) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.profileUrl = profileUrl;
        this.nickName = nickName;
        this.role = role;
        this.contents = contents;
    }
}
