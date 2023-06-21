package com.back.moment.boards.service;

import com.back.moment.boards.dto.*;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.BoardHashTag;
import com.back.moment.boards.entity.Tag_Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.boards.repository.BoardHashTagRepository;
import com.back.moment.boards.repository.Tag_BoardRepository;
import com.back.moment.boards.repository.boardSearch.BoardSearch;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.matching.entity.Matching;
import com.back.moment.matching.entity.MatchingApply;
import com.back.moment.matching.repository.MatchingApplyRepository;
import com.back.moment.matching.repository.MatchingRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardHashTagRepository boardHashTagRepository;
    private final Tag_BoardRepository tag_boardRepository;
    private final UsersRepository usersRepository;
    private final S3Uploader s3Uploader;
    private final BoardSearch boardSearch;
    private final MatchingApplyRepository matchingApplyRepository;
    private final MatchingRepository matchingRepository;

    // 게시글 생성
    @Transactional
    public ResponseEntity<Void> createBoard(BoardRequestDto boardRequestDto, Users users, MultipartFile boardImg){
        Board board = new Board();
        if(users.getRole() != null) {
            board.saveBoard(boardRequestDto, users);
            if (!boardImg.isEmpty()) {
                try {
                    String imgPath = s3Uploader.upload(boardImg);
                    board.setBoardImgUrl(imgPath);
                } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            boardRepository.save(board);

            if (boardRequestDto.getBoardHashTag() != null) {
                // 첫번째 문자에 #이 있는지 확인하는 메서드 호출
                boardRequestDto.setBoardHashTag(boardRequestDto.getBoardHashTag());

                for (String boardHashTag : boardRequestDto.getBoardHashTag()) {
                    String boardHashTagString = boardHashTag.substring(1);
                    BoardHashTag existTag = boardHashTagRepository.findByHashTag(boardHashTagString);
                    if (existTag != null) {
                        Tag_Board tag_board = new Tag_Board(existTag, board);
                        tag_boardRepository.save(tag_board);
                    } else {
                        BoardHashTag boardHashTagTable = new BoardHashTag(boardHashTagString);
                        boardHashTagRepository.save(boardHashTagTable);
                        Tag_Board tag_board = new Tag_Board(boardHashTagTable, board);
                        tag_boardRepository.save(tag_board);
                    }
                }
            }
        } else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_ROLE);
        }

        return ResponseEntity.ok(null);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<BoardListResponseDto> getAllBoards(Pageable pageable) {
        List<Board> modelBoardList = sortBoardList(boardRepository.getModelBoardListByHostIdWithFetch("MODEL"));
        List<Board> photographerBoardList = sortBoardList(boardRepository.getPhotographerBoardListByHostIdWithFetch("PHOTOGRAPHER"));

        Page<ModelBoardListResponseDto> modelBoardPage = createBoardPage(modelBoardList, pageable, ModelBoardListResponseDto::new);
        Page<PhotographerBoardListResponseDto> photographerBoardPage = createBoardPage(photographerBoardList, pageable, PhotographerBoardListResponseDto::new);

        boolean modelHasMorePage = modelBoardPage.hasNext();
        int modelCurrentPage = modelBoardPage.getNumber();
        int modelTotalPages = modelBoardList.isEmpty() ? modelBoardPage.getTotalPages() : modelBoardPage.getTotalPages() - 1;

        boolean photographerHasMorePage = photographerBoardPage.hasNext();
        int photographerCurrentPage = photographerBoardPage.getNumber();
        int photographerTotalPages = photographerBoardList.isEmpty() ? photographerBoardPage.getTotalPages() : photographerBoardPage.getTotalPages() - 1;

        return new ResponseEntity<>(
                new BoardListResponseDto(
                        modelBoardPage,
                        photographerBoardPage,
                        modelHasMorePage,
                        photographerHasMorePage,
                        modelCurrentPage,
                        modelTotalPages,
                        photographerCurrentPage,
                        photographerTotalPages
                ),
                HttpStatus.OK
        );
    }

    private List<Board> sortBoardList(List<Board> boardList) {
        return boardList.stream()
                .sorted(Comparator.comparing(board -> {
                    LocalDate deadLineDate = Optional.ofNullable(board)
                            .map(b -> (Board) b)
                            .map(Board::getDeadLine)
                            .map(LocalDate::parse)
                            .orElse(LocalDate.now());
                    return ChronoUnit.DAYS.between(deadLineDate, LocalDate.now());
                }).reversed())
                .collect(Collectors.toList());
    }


    private <T> Page<T> createBoardPage(List<Board> boardList, Pageable pageable, Function<Board, T> dtoMapper) {
        if (boardList.size() > pageable.getOffset()) {
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), boardList.size());
            List<T> dtoList = boardList.subList(startIndex, endIndex)
                    .stream()
                    .map(dtoMapper)
                    .toList();
            return new PageImpl<>(dtoList, pageable, boardList.size());
        } else {
            return new PageImpl<>(
                    boardList.stream()
                            .map(dtoMapper)
                            .toList(),
                    pageable,
                    boardList.size()
            );
        }
    }


    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<BoardDetailResponseDto> getBoard(Long boardId, Users users){
        Board board = existBoard(boardId);
        boolean checkApply = false;
        MatchingApply matchingApply = matchingApplyRepository.findByBoardIdAndApplicantId(boardId, users.getId());
        if(matchingApply != null) checkApply = true;

        return new ResponseEntity<>(new BoardDetailResponseDto(board, checkApply, board.getMatching()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> deleteBoard(Long boardId, Users users) {
        Board board = existBoard(boardId);

        existUser(users.getEmail());

        if(!Objects.equals(users.getId(), board.getUsers().getId())) {
            throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);
        }
        List<MatchingApply> matchingApplyList = matchingApplyRepository.findAllByBoardId(boardId);
        if(matchingApplyList != null)
            matchingApplyRepository.deleteAll(matchingApplyList);

        Matching matching = matchingRepository.findByBoardId(boardId);
        if(matching != null)
            matchingRepository.delete(matching);

        s3Uploader.delete(board.getBoardImgUrl());
        boardRepository.deleteById(boardId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> updateBoard(Long boardId, UpdateBoardRequestDto update, Users users){
        Board board = existBoard(boardId);
        if(!Objects.equals(board.getUsers().getId(), users.getId()))
            throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);

        board.updateBoard(update);
        boardRepository.save(board);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Page<BoardSearchListResponseDto>> searchBoard(String title, String location, String userNickName, String keyword, String role, Pageable pageable){
        Page<BoardSearchListResponseDto> boardPage = boardSearch.searchBoards(title, location, userNickName, keyword, role, pageable);

        return ResponseEntity.ok(boardPage);
    }

    // 유저 존재 확인
    public void existUser(String email){
        usersRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_USER)
        );
    }

    public Board existBoard(Long boardId){
        return boardRepository.findExistBoard(boardId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );
    }

}

