package com.back.moment.feed.service;

import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
import com.back.moment.love.repository.LoveRepository;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.recommend.repository.RecommendRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FeedServiceTest {

    @MockBean
    private PhotoRepository photoRepository;

    @MockBean
    private LoveRepository loveRepository;

    @MockBean
    private RecommendRepository recommendRepository;

    @MockBean
    private UsersRepository usersRepository;

    @Autowired
    private FeedService feedService;

    @Test
    void uploadImages() throws IOException {
        // Given
        String content = "테스트용";
        MockMultipartFile mockFile = new MockMultipartFile(
                "mockFile",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );
        List<MultipartFile> imageList = Collections.singletonList(mockFile);
        Users users = new Users();

        // Given
        S3Uploader s3Uploader = mock(S3Uploader.class);
        PhotoRepository photoRepository = mock(PhotoRepository.class);

        String imageUrl = "mockImageUrl";
        when(s3Uploader.upload(imageList.get(0))).thenReturn(imageUrl);

        // When
        ResponseEntity<Void> responseEntity = feedService.uploadImages(content, imageList, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    void lovePhoto() {
        // Given
        Long photoId = 1L;
        Users users = new Users();
        Photo photo = new Photo();
        photo.setId(1L);
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        // 의존성과 그 동작을 모킹한다

        // When
        ResponseEntity<String> responseEntity = feedService.lovePhoto(photoId, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성한다
    }

    @Test
    void recommendUser() {
        // Given
        String nickName = "user1";
        Users users = new Users();
        users.setNickName("user1");
        when(usersRepository.findByNickName(nickName)).thenReturn(Optional.of(users));

        // 의존성과 그 동작을 모킹한다

        // When
        ResponseEntity<String> responseEntity = feedService.recommendUser(nickName, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성한다
    }

    @Test
    void getAllFeeds() {

        // 의존성과 그 동작을 모킹한다

        // When
        ResponseEntity<FeedListResponseDto> responseEntity = feedService.getAllFeeds();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성한다
    }

    @Test
    void getFeed() {
        // Given
        Long photoId = 1L;
        Users users = new Users();
        users.setProfileImg("profileImgUrl");
        Photo photo = new Photo();
        photo.setId(1L);
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
        Users users1 = new Users();
        users1.setProfileImg("profileImgUrl");
        photo.setUsers(users1);

        // 의존성과 그 동작을 모킹한다

        // When
        ResponseEntity<FeedDetailResponseDto> responseEntity = feedService.getFeed(photoId, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성한다
    }

    @Test
    void writeContents() {
        // Given
        Long photoId = 1L;
        String content = "테스트용";
        Users users = new Users();
        users.setId(1L);
        Photo photo = new Photo();
        photo.setId(1L);
        Users users1 = new Users();
        users1.setId(1L);
        photo.setUsers(users1);
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
        assertEquals(users.getId(), photo.getUsers().getId());


        // When
        ResponseEntity<Void> responseEntity = feedService.writeContents(photoId, content, users);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 기대하는 동작을 확인하기 위해 추가적인 assert문을 작성한다


    }
}
