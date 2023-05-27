package com.back.moment.boards.service;

import com.back.moment.boards.dto.BoardDetailResponseDto;
import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.LocationTag;
import com.back.moment.boards.entity.Tag_Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.boards.repository.LocationTagRepository;
import com.back.moment.boards.repository.Tag_BoardRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.s3.S3Uploader;
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
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final LocationTagRepository locationTagRepository;
    private final Tag_BoardRepository tag_boardRepository;
    private final UsersRepository usersRepository;
    private final S3Uploader s3Uploader;

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

            if (boardRequestDto.getLocationTags() != null) {
                // 첫번째 문자에 #이 있는지 확인하는 메서드 호출
                boardRequestDto.setLocationTags(boardRequestDto.getLocationTags());

                for (String locationTag : boardRequestDto.getLocationTags()) {
                    String locationTagString = locationTag.substring(1);
                    LocationTag existTag = locationTagRepository.findByLocation(locationTagString);
                    if (existTag != null) {
                        Tag_Board tag_board = new Tag_Board(existTag, board);
                        tag_boardRepository.save(tag_board);
                    } else {
                        LocationTag locationTagTable = new LocationTag(locationTagString);
                        locationTagRepository.save(locationTagTable);
                        Tag_Board tag_board = new Tag_Board(locationTagTable, board);
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
    public ResponseEntity<Page<BoardListResponseDto>> getAllBoards(Users users, Pageable pageable) {
        existUser(users.getEmail());
        Page<BoardListResponseDto> boardList = boardRepository.selectAllBoard(pageable);

        if (boardList.hasNext()) {
            boardList = new PageImpl<>(boardList.getContent(), pageable, boardList.getTotalElements());
        } else {
            boardList = Page.empty(pageable);
        }

        return new ResponseEntity<>(boardList, HttpStatus.OK);
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
        return boardRepository.findById(boardId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );
    }
}
