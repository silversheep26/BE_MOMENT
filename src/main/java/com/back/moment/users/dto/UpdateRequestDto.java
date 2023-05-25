package com.back.moment.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {
    private String nickName;
    private String password;
}
