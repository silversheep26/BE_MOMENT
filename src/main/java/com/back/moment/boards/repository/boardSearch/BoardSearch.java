package com.back.moment.boards.repository.boardSearch;

import com.back.moment.boards.dto.BoardSearchListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    Page<BoardSearchListResponseDto> searchBoards(String title, String location, String userNickName, String keyWord, String role, Pageable pageable);
}
