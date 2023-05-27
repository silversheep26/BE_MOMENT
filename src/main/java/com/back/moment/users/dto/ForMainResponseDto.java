package com.back.moment.users.dto;

import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Getter
@NoArgsConstructor
public class ForMainResponseDto {
    private Long userId;
    private String nickName;
    private RoleEnum role;
    private String profileUrl;
    private List<OnlyPhotoResponseDto> photoList;

    public ForMainResponseDto(Users users) {
        this.userId = users.getId();
        this.nickName = users.getNickName();
        this.role = users.getRole();
        this.profileUrl = users.getProfileImg();
        this.photoList = users.getPhotoList().stream()
                .map(OnlyPhotoResponseDto::new)
                .sorted(Comparator.comparingInt(OnlyPhotoResponseDto::getLoveCnt).reversed())
                .limit(3)
                .toList();
    }

}
