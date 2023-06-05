package com.back.moment.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {
    private String nickName;
    private String password;
    private String role;
}