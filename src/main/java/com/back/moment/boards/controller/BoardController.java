package com.back.moment.boards.controller;

import com.back.moment.boards.dto.*;
import com.back.moment.boards.service.BoardService;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("")  // 게시글작성
    public ResponseEntity<Void> createBoard(@RequestPart BoardRequestDto boardRequestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestPart MultipartFile boardImg){
        return boardService.createBoard(boardRequestDto, userDetails.getUsers(), boardImg);
    }


    @GetMapping("")
    public ResponseEntity<BoardListResponseDto> getBoardsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return boardService.getAllBoards(pageable);
    }

    // 게시글 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponseDto> getBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.getBoard(boardId, userDetails.getUsers());
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.deleteBoard(boardId, userDetails.getUsers());
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<Void> updateBoard(@PathVariable Long boardId,
                                            @RequestBody UpdateBoardRequestDto update,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.updateBoard(boardId, update, userDetails.getUsers());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BoardSearchListResponseDto>> boardSearch(@RequestParam(required = false) String title,
                                                                        @RequestParam(required = false) String location,
                                                                        @RequestParam(required = false) String userNickName,
                                                                        @RequestParam(required = false) String keyWord,
                                                                        @RequestParam(required = false) String role,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return boardService.searchBoard(title, location, userNickName, keyWord, role, pageable);
    }
}


