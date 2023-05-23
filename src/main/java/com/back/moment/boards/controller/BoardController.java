package com.back.moment.boards.controller;

import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.boards.service.BoardService;
import com.back.moment.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("")
    public ResponseEntity<Void> createBoard(@RequestBody BoardRequestDto boardRequestDto){
        return boardService.createBoard(boardRequestDto, new Users());
    }
}
