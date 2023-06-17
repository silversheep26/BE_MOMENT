package com.back.moment.photos.repository.getAllOnlyPhoto;

import com.back.moment.photos.dto.OnlyPhotoResponseDto;

import java.util.List;

public interface GetAllOnlyPhoto {
    List<OnlyPhotoResponseDto> findAllOnlyPhoto(Long userId);
}
