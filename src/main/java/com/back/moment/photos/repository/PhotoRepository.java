package com.back.moment.photos.repository;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.dto.PhotoMyPageResponseDto;
import com.back.moment.photos.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    @Query("select new com.back.moment.photos.dto.PhotoFeedResponseDto(p) from Photo p")
    List<PhotoFeedResponseDto> getAllPhoto();

    @Query("select new com.back.moment.photos.dto.PhotoMyPageResponseDto(p.imagUrl, p.loveCnt) from Photo p")
    List<PhotoMyPageResponseDto> getAllOnlyPhoto();
}
