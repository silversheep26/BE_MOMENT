package com.back.moment.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeRequestDto {
    private String email;
    private String code;

}
