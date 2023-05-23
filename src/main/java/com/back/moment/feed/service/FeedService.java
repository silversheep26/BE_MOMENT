package com.back.moment.feed.service;

import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final S3Uploader s3Uploader;
    private final PhotoRepository photoRepository;
    public ResponseEntity<Void> uploadImages(List<MultipartFile> imageList, Users users) throws IOException {
        for(MultipartFile image : imageList){
            String imageUrl = s3Uploader.upload(image);
            Photo photo = new Photo(users, imageUrl);
            photoRepository.save(photo);
        }
        return ResponseEntity.ok(null);
    }
}
