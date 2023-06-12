package com.back.moment.feed.dto;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class FeedListResponseDto {
    private List<PhotoFeedResponseDto> photoList1;
    private List<PhotoFeedResponseDto> photoList2;
    private boolean hasMorePages;
    int currentPage;
    int totalPages;

    public FeedListResponseDto(List<PhotoFeedResponseDto> photoList1, List<PhotoFeedResponseDto> photoList2, boolean hasMorePages, int currentPage, int totalPages) {
        this.photoList1 = photoList1;
        this.photoList2 = photoList2;
        this.hasMorePages = hasMorePages;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
}
