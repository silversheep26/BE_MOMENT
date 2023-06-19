package com.back.moment.photos.repository.feedSearch;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import org.springframework.data.domain.Page;

public interface FeedSearch {
    Page<PhotoFeedResponseDto> feedSearch(String userNickName, String tag);
}
