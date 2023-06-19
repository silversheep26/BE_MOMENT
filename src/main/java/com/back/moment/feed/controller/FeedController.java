package com.back.moment.feed.controller;

import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
//import com.back.moment.feed.dto.FeedRequestDto;
import com.back.moment.feed.dto.LoveCheckResponseDto;
import com.back.moment.feed.dto.UsersInLoveListResponseDto;
import com.back.moment.feed.service.FeedService;
import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> uploadImages(@RequestPart String contents,
                                             @RequestPart List<String> photoHashTag,
                                             @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFile,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return feedService.uploadImages(contents, photoHashTag, imageFile, userDetails.getUsers());
    }

    // Feed 에서 photo 좋아요
    @PostMapping("/love/{photoId}")
    public ResponseEntity<LoveCheckResponseDto> lovePhoto(@PathVariable Long photoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.lovePhoto(photoId, userDetails.getUsers());
    }

    // feed 에서 회원 추천
//    @PostMapping("/recommend/{nickName}")
//    public ResponseEntity<String> recommendUser(@PathVariable String nickName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return feedService.recommendUser(nickName, userDetails.getUsers());
//    }

    // feed 전체 조회
    @GetMapping("")
    public ResponseEntity<FeedListResponseDto> getAllFeeds(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "16") int size,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        Pageable pageable = PageRequest.of(page, size);
        return feedService.getAllFeeds(pageable, userDetails != null ? userDetails.getUsers() : null);
    }

    // Feed 상세 조회
    @GetMapping("/{photoId}")
    public ResponseEntity<FeedDetailResponseDto> getFeed(@PathVariable Long photoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.getFeed(photoId, userDetails.getUsers());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PhotoFeedResponseDto>> photoSearch(@RequestParam(required = false) String userNickName,
                                                                  @RequestParam(required = false) String tag,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "16") int size){
        Pageable pageable = PageRequest.of(page, size);
        return feedService.searchPhoto(tag, userNickName, pageable, userDetails != null ? userDetails.getUsers() : null);
    }

    // feed 내용 작성
    @PutMapping("/{photoId}")
    public ResponseEntity<Void> writeContents(@PathVariable Long photoId,
                                              @RequestBody String content,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return feedService.writeContents(photoId, content, userDetails.getUsers());
    }

    @GetMapping("/love-check/{photoId}")
    public ResponseEntity<List<UsersInLoveListResponseDto>> whoLoveCheck(@PathVariable Long photoId){
        return feedService.whoLoveCheck(photoId);
    }
}
