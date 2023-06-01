package com.back.moment.users.dto;

import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {
    private String nickName;
    private String password;
    private String role;
}