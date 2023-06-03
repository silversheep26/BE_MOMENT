package com.back.moment.email.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CodeRequestDto {
    private String email;
    private String code;

    public CodeRequestDto(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
