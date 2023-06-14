package com.back.moment.boards.repository;

import com.back.moment.boards.dto.MyPageBoardListResponseDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select new com.back.moment.boards.dto.MyPageBoardListResponseDto(b) from Board b order by b.createdAt desc")
    List<MyPageBoardListResponseDto> selectAllBoardList();

    @Query("select new com.back.moment.boards.dto.MyPageBoardListResponseDto(b) from Board b where b.users.role = :role order by b.createdAt desc")
    List<MyPageBoardListResponseDto> selectAllEachRoleBoardList(@Param("role") RoleEnum role);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.tag_boardList tb left JOIN FETCH tb.boardHashTag tbl WHERE b.users.id = :hostId order by b.createdAt desc")
    List<Board> getBoardListByHostIdWithFetch(@Param("hostId") Long hostId);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.tag_boardList tb left JOIN FETCH tb.boardHashTag tbl WHERE b.users.role = :role order by b.createdAt desc")
    List<Board> getModelBoardListByHostIdWithFetch(@Param("role") RoleEnum role);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.tag_boardList tb left JOIN FETCH tb.boardHashTag tbl WHERE b.users.role = :role order by b.createdAt desc")
    List<Board> getPhotographerBoardListByHostIdWithFetch(@Param("role") RoleEnum role);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.tag_boardList tb left JOIN FETCH tb.boardHashTag tbl WHERE b.id = :boardId")
    Optional<Board> findExistBoard(@Param("boardId") Long boardId);

    List<Board> findByUsers(Users users);
}
