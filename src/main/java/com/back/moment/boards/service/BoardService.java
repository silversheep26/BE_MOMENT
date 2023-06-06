package com.back.moment.boards.service;

import com.back.moment.boards.dto.*;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.BoardHashTag;
import com.back.moment.boards.entity.Tag_Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.boards.repository.BoardHashTagRepository;
import com.back.moment.boards.repository.Tag_BoardRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardHashTagRepository boardHashTagRepository;
    private final Tag_BoardRepository tag_boardRepository;
    private final UsersRepository usersRepository;
    private final S3Uploader s3Uploader;

    // 게시글 생성
    @Transactional
    public ResponseEntity<Void> createBoard(BoardRequestDto boardRequestDto, Users users, MultipartFile boardImg){
        Board board = new Board();
        if(users.getRole() != RoleEnum.NONE) {

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

    // 게시글 전체 조회
//    @Transactional(readOnly = true)
//    public ResponseEntity<Page<BoardListResponseDto>> getAllBoards(Users users, Pageable pageable){
//        existUser(users.getEmail());
//        Page<BoardListResponseDto> boardList = boardRepository.selectAllBoard(pageable);
//        return new ResponseEntity<>(boardList, HttpStatus.OK);
//    }

    @Transactional(readOnly = true)
    public ResponseEntity<BoardListResponseDto> getAllBoards(Pageable pageable) {

        List<Board> modelBoardList = boardRepository.getModelBoardListByHostIdWithFetch(RoleEnum.MODEL);
        List<Board> photographerBoardList = boardRepository.getPhotographerBoardListByHostIdWithFetch(RoleEnum.PHOTOGRAPHER);
        Page<ModelBoardListResponseDto> modelBoardPage;
        Page<PhotographerBoardListResponseDto> photographerBoardPage;

        modelBoardList.sort(Comparator.comparing(Board::getCreatedAt).reversed());

        if (modelBoardList.size() > pageable.getOffset()) {
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), modelBoardList.size());
            List<ModelBoardListResponseDto> modelBoardDtoList = modelBoardList.subList(startIndex, endIndex)
                    .stream()
                    .map(ModelBoardListResponseDto::new)
                    .toList();
            modelBoardPage = new PageImpl<>(modelBoardDtoList, pageable, modelBoardList.size());
        } else{
            modelBoardPage = new PageImpl<>(
                    modelBoardList.stream()
                            .map(ModelBoardListResponseDto::new)
                            .toList(),
                    pageable,
                    modelBoardList.size()
            );
        }
        boolean modelHasMorePage = modelBoardPage.hasNext();
        int modelCurrentPage = modelBoardPage.getNumber();
        int modelTotalPages;
        if(modelBoardList.isEmpty())
            modelTotalPages = modelBoardPage.getTotalPages();
        else modelTotalPages = modelBoardPage.getTotalPages() - 1;

        photographerBoardList.sort(Comparator.comparing(Board::getCreatedAt).reversed());
        if (photographerBoardList.size() > pageable.getOffset()) {
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), photographerBoardList.size());
            List<PhotographerBoardListResponseDto> photographerBoardDtoList = photographerBoardList.subList(startIndex, endIndex)
                    .stream()
                    .map(PhotographerBoardListResponseDto::new)
                    .toList();
            photographerBoardPage = new PageImpl<>(photographerBoardDtoList, pageable, photographerBoardList.size());
        } else{
            photographerBoardPage = new PageImpl<>(
                    photographerBoardList.stream()
                            .map(PhotographerBoardListResponseDto::new)
                            .toList(),
                    pageable,
                    photographerBoardList.size()
            );
        }
        boolean photographerHasMorePage = photographerBoardPage.hasNext();
        int photographerCurrentPage = photographerBoardPage.getNumber();
        int photographerTotalPages;
        if(photographerBoardList.isEmpty())
            photographerTotalPages = photographerBoardPage.getTotalPages();
        else photographerTotalPages = photographerBoardPage.getTotalPages() - 1;

        return new ResponseEntity<>(new BoardListResponseDto(modelBoardPage,
                photographerBoardPage,
                modelHasMorePage,
                photographerHasMorePage,
                modelCurrentPage,
                modelTotalPages,
                photographerCurrentPage,
                photographerTotalPages), HttpStatus.OK);
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<BoardDetailResponseDto> getBoard(Long boardId, Users users){
        Board board = existBoard(boardId);

        return new ResponseEntity<>(new BoardDetailResponseDto(board), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> deleteBoard(Long boardId, Users users) {
        Board board = existBoard(boardId);

        existUser(users.getEmail());

        if(!Objects.equals(users.getId(), board.getUsers().getId())) {
            throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);
        }
        s3Uploader.delete(board.getBoardImgUrl());
        boardRepository.deleteById(boardId);

        return new ResponseEntity<>(HttpStatus.OK);
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
