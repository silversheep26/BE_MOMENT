package com.back.moment.boards.controller;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.boards.service.BoardService;
import com.back.moment.users.entity.Users;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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

    @GetMapping("") //게시글 전체 조회
    public ResponseEntity<Page<BoardListResponseDto>> getAllBoards(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.getAllBoards(userDetails.getUsers(), pageable);
    }
}
