package com.back.moment.feed.dto;

import com.back.moment.users.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@NoArgsConstructor
public class UsersInLoveListResponseDto {
    private Long userId;
    private String nickName;
    private String profileUrl;

    public UsersInLoveListResponseDto(Users users){
        this.userId = users.getId();
        this.nickName = users.getNickName();
        this.profileUrl = users.getProfileImg();
    }
}
