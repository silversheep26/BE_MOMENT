package com.back.moment.users.controller;

import com.back.moment.users.dto.MyPageResponseDto;
import com.back.moment.users.dto.UpdateRequestDto;
import com.back.moment.users.security.UserDetailsImpl;
import com.back.moment.users.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/{hostId}")
    public ResponseEntity<MyPageResponseDto> getMyPage(@PathVariable Long hostId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPage(hostId, userDetails.getUsers());
    }

    @DeleteMapping ("/{photoId}")
    public ResponseEntity<String> deletePhoto(@PathVariable Long photoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.deletePhoto(photoId, userDetails.getUsers());
    }

    @PutMapping(value = "/{hostId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePage(@PathVariable Long hostId,
                                           @Valid @RequestPart(value = "update", required = false) UpdateRequestDto updateRequestDto,
                                           @Validated @RequestPart(value = "update", required = false) UpdateRequestDto updateRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestPart(value = "profile", required = false) MultipartFile profileImg) throws IOException {
        return myPageService.updateMyPage(hostId, updateRequestDto, userDetails.getUsers(), profileImg);
    }
}
