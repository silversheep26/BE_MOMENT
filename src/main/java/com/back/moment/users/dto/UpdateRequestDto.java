package com.back.moment.users.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {

    @Size(min = 2, max = 8, message = "닉네임은 2글자 이상, 8글자 이하만 가능합니다.")
    private String nickName;
    private String password;
    private String role;
}