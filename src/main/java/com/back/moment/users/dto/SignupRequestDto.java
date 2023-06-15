package com.back.moment.users.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {
    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", message = "이메일 형식에 맞춰 작성해주세요.")
    @NotNull(message = "email을 입력해주세요")
    private String email;

    @Size(min = 8, max = 15, message = "password는 8 이상, 15 이하만 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z\\p{Punct}0-9]*$", message = "password는 알파벳 대소문자, 특수문자, 숫자만 가능합니다.")
    @NotNull(message = "password를 입력해주세요")
    private String password;

    @Size(min = 2, max = 8, message = "닉네임은 2글자 이상, 8글자 이하만 가능합니다.")
    private String nickName;

    private String gender;

    private String role;

}
