package com.back.moment.boards.repository;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select new com.back.moment.boards.dto.BoardListResponseDto(b) from Board b order by b.createdAt desc")
    Page<BoardListResponseDto> selectAllBoard(Pageable pageable);

    @Query("select new com.back.moment.boards.dto.BoardListResponseDto(b) from Board b order by b.createdAt desc")
    List<BoardListResponseDto> selectAllBoardList();
}
