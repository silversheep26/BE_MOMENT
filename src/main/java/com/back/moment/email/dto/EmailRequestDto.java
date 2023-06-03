package com.back.moment.email.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailRequestDto {
    private String email;

    public EmailRequestDto(String email) {
        this.email = email;
    }
}
