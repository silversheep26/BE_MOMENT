package com.back.moment.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long userId;
    private String nickName;
    private String profileImg;
    private String role;

}
