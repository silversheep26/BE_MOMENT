package com.back.moment.feed.dto;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FeedListResponseDto {
    private List<PhotoFeedResponseDto> photoList;

    public FeedListResponseDto(List<PhotoFeedResponseDto> photoList) {
        this.photoList = photoList;
    }
}
