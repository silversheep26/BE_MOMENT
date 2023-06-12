package com.back.moment.feed.dto;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class FeedListResponseDto {
    private Page<PhotoFeedResponseDto> photoList1;
    private Page<PhotoFeedResponseDto> photoList2;
    private boolean hasMorePages;
    int currentPage;
    int totalPages;

    public FeedListResponseDto(Page<PhotoFeedResponseDto> photoList1, Page<PhotoFeedResponseDto> photoList2, boolean hasMorePages, int currentPage, int totalPages) {
        this.photoList1 = photoList1;
        this.photoList2 = photoList2;
        this.hasMorePages = hasMorePages;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
}
