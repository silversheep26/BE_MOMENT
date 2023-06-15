package com.back.moment.boards.repository.boardSearch;

import com.back.moment.boards.dto.BoardSearchListResponseDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.QBoard;
import com.back.moment.boards.entity.QBoardHashTag;
import com.back.moment.boards.entity.QTag_Board;
import com.back.moment.users.entity.QUsers;
import com.back.moment.users.entity.RoleEnum;
import com.mongodb.internal.connection.QueryResult;
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
    public Page<BoardSearchListResponseDto> searchBoards(String location, String userNickName, String keyWord, RoleEnum role, Pageable pageable){
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

        if (role != null) {
            query.where(users.role.eq(role));
        } else {
            query.where(users.role.eq(RoleEnum.PHOTOGRAPHER).or(users.role.eq(RoleEnum.MODEL)));
        }

        if(location != null){
            query.where(board.location.eq(location));
        }
        if(userNickName != null){
            query.where(users.nickName.eq(userNickName));
        }
        if(keyWord != null){
            query.where(boardHashTag.hashTag.eq(keyWord));
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
