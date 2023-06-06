package com.back.moment.boards.repository;

import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.Tag_Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Tag_BoardRepository extends JpaRepository<Tag_Board, Long> {
    @Query("select tb.board from Tag_Board tb where tb.boardHashTag.id = :locationTagId")
    List<Board> selectBoardByTag(Long locationTagId);
}
