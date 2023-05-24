package com.back.moment.mainPage.dto;

import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BeforeLogInResponseDto {
    private List<OnlyPhotoResponseDto> photoList;

    public BeforeLogInResponseDto(List<OnlyPhotoResponseDto> photoList){
        this.photoList = photoList;
    }
}
