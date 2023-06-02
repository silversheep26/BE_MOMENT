package com.back.moment.boards.service;

import com.back.moment.boards.dto.BoardDetailResponseDto;
import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.boards.repository.LocationTagRepository;
import com.back.moment.boards.repository.Tag_BoardRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private LocationTagRepository locationTagRepository;

    @MockBean
    private Tag_BoardRepository tag_boardRepository;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private S3Uploader s3Uploader;

    @Test
    void createBoard() {
        // Given
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        Users users = new Users();
        MultipartFile boardImg = new MockMultipartFile("boardImg", new byte[0]);
        users.setRole(RoleEnum.MODEL);

        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정

        // When
        ResponseEntity<Void> responseEntity = boardService.createBoard(boardRequestDto, users, boardImg);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성
    }

//    @Test
//    void getAllBoards() {
//        // Given
//        Users users = new Users();
//        users.setEmail("test@example.com");
//
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // 적절한 테스트 데이터로 mockBoardList 생성
//        List<BoardListResponseDto> mockBoardList = new ArrayList<>();
//        Board board1 = new Board();
//        board1.setTitle("게시글 1");
//
//        Users users1 = new Users();
//        users1.setRole(RoleEnum.MODEL);
//        board1.setUsers(users1);
//        // board1에 필요한 속성들을 적절한 값으로 설정
//        BoardListResponseDto dto1 = new BoardListResponseDto(board1);
//        mockBoardList.add(dto1);
//
//        Board board2 = new Board();
//        board2.setTitle("게시글 2");
//
//        Users users2 = new Users();
//        users2.setRole(RoleEnum.MODEL);
//        board2.setUsers(users2);
//        // board2에 필요한 속성들을 적절한 값으로 설정
//        BoardListResponseDto dto2 = new BoardListResponseDto(board2);
//        mockBoardList.add(dto2);
//
//        // Page 객체로 변환하여 mockBoardList 생성
//        Page<BoardListResponseDto> pageMockBoardList = new PageImpl<>(mockBoardList, pageable, mockBoardList.size());
//
//        // boardRepository.selectAllBoard() 메서드가 pageMockBoardList를 반환하도록 설정
//        when(boardRepository.selectAllBoard(pageable)).thenReturn(pageMockBoardList);
//
//        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정
//        // usersRepository.findByEmail이 항상 값이 존재하는 경우를 가정하여 반환 값을 설정
//        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(users));
//
//        // When
//        ResponseEntity<Page<BoardListResponseDto>> responseEntity = boardService.getAllBoards(users, pageable);
//
//        // Then
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성
//    }

    @Test
    void getBoard() {
        // Given
        Long boardId = 1L;
        Users users = new Users();

        // boardRepository를 모킹합니다.
        Board board = new Board(); // 모킹된 Board 객체를 생성합니다.
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));// findById() 메서드가 호출될 때 모킹된 Board 객체를 반환하도록 설정합니다.
        Users users1 = new Users();
        users1.setNickName("아무개");
        board.setUsers(users1);

        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정

        // When
        ResponseEntity<BoardDetailResponseDto> responseEntity = boardService.getBoard(boardId, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성

    }

    @Test
    void deleteBoard() {
        // Given
        Long boardId = 1L;
        Users users = new Users();
        users.setEmail("test@example.com");
        users.setId(1L);
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(users));

        // boardRepository를 모킹합니다.
        Board board = new Board(); // 모킹된 Board 객체를 생성합니다.
        Users users1 = new Users();
        users1.setId(1L);
        board.setUsers(users1);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));// findById() 메서드가 호출될 때 모킹된 Board 객체를 반환하도록 설정합니다.


        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정
        // When
        ResponseEntity<Void> responseEntity = boardService.deleteBoard(boardId, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성
    }

    @Test
    void existUser() {
        // Given
        String email = "test@example.com";

        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정

        // When
        Throwable exception = assertThrows(ApiException.class, () -> boardService.existUser(email));

        // Then
        // 예상한 ApiException이 발생하는지 확인하는 assert문 작성
    }

    @Test
    void existBoard() {
        // Given
        Long boardId = 1L;

        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정

        // When
        Throwable exception = assertThrows(ApiException.class, () -> boardService.existBoard(boardId));

        // Then
        // 예상한 ApiException이 발생하는지 확인하는 assert문 작성
    }
}
