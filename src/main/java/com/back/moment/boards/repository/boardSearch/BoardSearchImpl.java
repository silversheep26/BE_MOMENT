package com.back.moment.boards.repository.boardSearch;

import com.back.moment.boards.dto.BoardSearchListResponseDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.QBoard;
import com.back.moment.boards.entity.QBoardHashTag;
import com.back.moment.boards.entity.QTag_Board;
import com.back.moment.users.entity.QUsers;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@Primary
public class BoardSearchImpl implements BoardSearch{
    private final JPAQueryFactory queryFactory;

    public BoardSearchImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<BoardSearchListResponseDto> searchBoards(String title, String location, String userNickName, String keyWord, String role, Pageable pageable){
        QBoard board = QBoard.board;
        QUsers users = QUsers.users;
        QTag_Board tag_board = QTag_Board.tag_Board;
        QBoardHashTag boardHashTag = QBoardHashTag.boardHashTag;

        JPQLQuery<Board> query = queryFactory
                .selectDistinct(board)
                .from(board)
                .leftJoin(board.users, users)
                .leftJoin(board.tag_boardList, tag_board)
                .leftJoin(tag_board.boardHashTag, boardHashTag);

        if(title != null){
            query.where(board.title.like("%"+ title + "%"));
        }
        if (location != null) {
            query.where(board.location.like("%" + location + "%"));
        }
        if (userNickName != null) {
            query.where(users.nickName.like("%" + userNickName + "%"));
        }
        if (keyWord != null) {
            query.where(boardHashTag.hashTag.like("%" + keyWord + "%"));
        }
        if (role != null) {
            switch (role) {
                case "PHOTOGRAPHER" -> query.where(board.role.eq("PHOTOGRAPHER"));
                case "MODEL" -> query.where(board.role.eq("MODEL"));
                case "BOTH" -> query.where(board.role.in("PHOTOGRAPHER", "MODEL"));
            }
        }


        QueryResults<Board> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Board> boardList = results.getResults();
        long totalCount = results.getTotal();

        List<BoardSearchListResponseDto> responseDtoList = boardList.stream()
                .map(BoardSearchListResponseDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtoList, pageable, totalCount);
    }
}
