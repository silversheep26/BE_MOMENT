package com.back.moment.boards.repository;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.dto.ModelBoardListResponseDto;
import com.back.moment.boards.dto.MyPageBoardListResponseDto;
import com.back.moment.boards.dto.PhotographerBoardListResponseDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.users.entity.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select new com.back.moment.boards.dto.ModelBoardListResponseDto(b) from Board b where b.role = :role order by b.createdAt desc")
    Page<ModelBoardListResponseDto> selectAllModelBoard(@Param("role") RoleEnum role, Pageable pageable);

    @Query("select new com.back.moment.boards.dto.PhotographerBoardListResponseDto(b) from Board b where b.role = :role order by b.createdAt desc")
    Page<PhotographerBoardListResponseDto> selectAllPhotographerBoard(@Param("role") RoleEnum role, Pageable pageable);

    @Query("select new com.back.moment.boards.dto.MyPageBoardListResponseDto(b) from Board b order by b.createdAt desc")
    List<MyPageBoardListResponseDto> selectAllBoardList();

    @Query("select new com.back.moment.boards.dto.MyPageBoardListResponseDto(b) from Board b where b.users.role = :role order by b.createdAt desc")
    List<MyPageBoardListResponseDto> selectAllEachRoleBoardList(@Param("role") RoleEnum role);

    @Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.tag_boardList tb JOIN FETCH tb.locationTag tbl WHERE tb.locationTag.id = tbl.id and  b.users.id = :hostId")
    List<Board> getBoardListByHostIdWithFetch(@Param("hostId") Long hostId);
}
