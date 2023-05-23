package com.back.moment.boards.service;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.LocationTag;
import com.back.moment.boards.entity.Tag_Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.boards.repository.LocationTagRepository;
import com.back.moment.boards.repository.Tag_BoardRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final LocationTagRepository locationTagRepository;
    private final Tag_BoardRepository tag_boardRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public ResponseEntity<Void> createBoard(BoardRequestDto boardRequestDto, Users users, MultipartFile boardImg){
        Board board = new Board();
        board.saveBoard(boardRequestDto, users);
        if(!boardImg.isEmpty()) {
            try {
                String imgPath = s3Uploader.upload(boardImg);
                board.setBoardImgUrl(imgPath);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        boardRepository.save(board);

        if(boardRequestDto.getLocationTags() != null){
            // 첫번째 문자에 #이 있는지 확인하는 메서드 호출
            boardRequestDto.setLocationTags(boardRequestDto.getLocationTags());

            for(String locationTag : boardRequestDto.getLocationTags()){
                String locationTagString = locationTag.substring(1);
                LocationTag existTag = locationTagRepository.findByLocation(locationTagString);
                if(existTag != null){
                    Tag_Board tag_board = new Tag_Board(existTag, board);
                    tag_boardRepository.save(tag_board);
                } else{
                    LocationTag locationTagTable = new LocationTag(locationTagString);
                    locationTagRepository.save(locationTagTable);
                    Tag_Board tag_board = new Tag_Board(locationTagTable, board);
                    tag_boardRepository.save(tag_board);
                }
            }
        }

        return ResponseEntity.ok(null);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Page<BoardListResponseDto>> getAllBoards(Users users, Pageable pageable){
        existUser(users.getEmail());
        Page<BoardListResponseDto> boardList = boardRepository.selectAllBoard(pageable);
        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }

    public void existUser(String email){
        userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_USER)
        );
    }
}
