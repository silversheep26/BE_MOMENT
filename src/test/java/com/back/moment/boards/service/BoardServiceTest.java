package com.back.moment.boards.service;

import com.back.moment.boards.dto.*;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.boards.repository.BoardHashTagRepository;
import com.back.moment.boards.repository.Tag_BoardRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private BoardHashTagRepository boardHashTagRepository;

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
        users.setRole("MODEL");

        // 필요한 mock 동작을 when().thenReturn()을 사용하여 설정

        // When
        ResponseEntity<Void> responseEntity = boardService.createBoard(boardRequestDto, users, boardImg);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성
    }

    @Test
    void getAllBoards() {
        // Given

        Pageable pageable = mock(Pageable.class);
        List<Board> modelBoardList = new ArrayList<>();
        // 모델 게시판 데이터 추가

        List<Board> photographerBoardList = new ArrayList<>();
        // 포토그래퍼 게시판 데이터 추가

        // boardRepository의 getModelBoardListByHostIdWithFetch 메서드가 호출될 때 mock 데이터 반환하도록 설정
        when(boardRepository.getModelBoardListByHostIdWithFetch("MODEL")).thenReturn(modelBoardList);
        // boardRepository의 getPhotographerBoardListByHostIdWithFetch 메서드가 호출될 때 mock 데이터 반환하도록 설정
        when(boardRepository.getPhotographerBoardListByHostIdWithFetch("PHOTOGRAPHER")).thenReturn(photographerBoardList);

        // 테스트할 메서드 호출
        ResponseEntity<BoardListResponseDto> response = boardService.getAllBoards(pageable);

        // 결과 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BoardListResponseDto responseBody = response.getBody();

        // modelBoardList와 modelBoardPage의 일치 여부 검증
        Page<ModelBoardListResponseDto> modelBoardPage = responseBody.getModelBoard();
        assertEquals(modelBoardList.size(), modelBoardPage.getTotalElements());

        List<ModelBoardListResponseDto> modelBoardDtoList = modelBoardPage.getContent();
        assertEquals(modelBoardList.size(), modelBoardDtoList.size());
        // modelBoardDtoList의 내용을 추가적으로 검증할 수 있는 로직 작성

        // photographerBoardList와 photographerBoardPage의 일치 여부 검증
        Page<PhotographerBoardListResponseDto> photographerBoardPage = responseBody.getPhotographerBoard();
        assertEquals(photographerBoardList.size(), photographerBoardPage.getTotalElements());

        List<PhotographerBoardListResponseDto> photographerBoardDtoList = photographerBoardPage.getContent();
        assertEquals(photographerBoardList.size(), photographerBoardDtoList.size());
        // photographerBoardDtoList의 내용을 추가적으로 검증할 수 있는 로직 작성
    }

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
