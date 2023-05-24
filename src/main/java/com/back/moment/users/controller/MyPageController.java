package com.back.moment.users.controller;

import com.back.moment.users.dto.MyPageResponseDto;
import com.back.moment.users.security.UserDetailsImpl;
import com.back.moment.users.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/{nickName}")
    public ResponseEntity<MyPageResponseDto> getMyPage(@PathVariable String nickName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPage(nickName, userDetails.getUsers());
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<String> deletePhoto(@PathVariable Long photoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.deletePhoto(photoId, userDetails.getUsers());
    }
}
