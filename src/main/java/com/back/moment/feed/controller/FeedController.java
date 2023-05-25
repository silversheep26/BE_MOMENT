package com.back.moment.feed.controller;

import com.amazonaws.Response;
import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
import com.back.moment.feed.dto.FeedRequestDto;
import com.back.moment.feed.service.FeedService;
import com.back.moment.users.entity.Users;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    // feed 업로드
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> uploadImages(@RequestPart(value = "contents",required = false) FeedRequestDto feedRequestDto,
                                             @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFile,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return feedService.uploadImages(feedRequestDto, imageFile, userDetails.getUsers());
    }

    // Feed 에서 photo 좋아요
    @PostMapping("/love/{photoId}")
    public ResponseEntity<String> lovePhoto(@PathVariable Long photoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.lovePhoto(photoId, userDetails.getUsers());
    }

    // feed 에서 회원 추천
    @PostMapping("/recommend/{nickName}")
    public ResponseEntity<String> recommendUser(@PathVariable String nickName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.recommendUser(nickName, userDetails.getUsers());
    }

    // feed 전체 조회
    @GetMapping("")
    public ResponseEntity<FeedListResponseDto> getAllFeeds(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return feedService.getAllFeeds(userDetails.getUsers());
    }

    // Feed 상세 조회
    @GetMapping("/{photoId}")
    public ResponseEntity<FeedDetailResponseDto> getFeed(@PathVariable Long photoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.getFeed(photoId, userDetails.getUsers());
    }

    // feed 내용 작성
    @PutMapping("/{photoId}")
    public ResponseEntity<Void> writeContents(@PathVariable Long photoId,
                                              @RequestBody FeedRequestDto feedRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.writeContents(photoId, feedRequestDto, userDetails.getUsers());
    }

}
