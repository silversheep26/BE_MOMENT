package com.back.moment.feed.controller;

import com.back.moment.feed.service.FeedService;
import com.back.moment.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> uploadImages(@RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFile) throws IOException {
        return feedService.uploadImages(imageFile, new Users());
    }
}
