package com.back.moment.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String email;
    private String nickName;
    private String gender;
    private String profileImg;

    public KakaoUserInfoDto(Long id, String email, String nickName, String gender, String profileImg) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.gender = gender;
        this.profileImg = profileImg;
    }
}