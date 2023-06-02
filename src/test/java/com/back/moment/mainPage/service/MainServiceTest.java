package com.back.moment.mainPage.service;

import com.back.moment.mainPage.dto.AfterLogInResponseDto;
import com.back.moment.mainPage.dto.BeforeLogInResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class MainServiceTest {

    @Autowired
    private MainService mainService;

    @MockBean
    private PhotoRepository photoRepository;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    void getMainPageSource() {
        // Given
        List<OnlyPhotoResponseDto> photoList = new ArrayList<>();
        // photoList에 적절한 데이터를 추가

        when(photoRepository.getAllOnlyPhoto()).thenReturn(photoList);

        // When
        ResponseEntity<BeforeLogInResponseDto> responseEntity = mainService.getMainPageSource();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // 적절한 응답 검증을 수행
    }

//    @Test
//    void getHomePageSource() {
//        // Given
//        Users users = new Users();
//        // users에 적절한 데이터를 추가
//
//        Pageable pageable = PageRequest.of(0, 3);
//
//        // 모의 객체(usersRepository)를 사용하여 메서드 호출 시 반환할 값을 설정
//        when(usersRepository.findTop3Photographer(users.getRole(), pageable)).thenReturn(new ArrayList<>());
//        when(usersRepository.findTop3Model(users.getRole(), pageable)).thenReturn(new ArrayList<>());
//        when(usersRepository.findTop3(pageable)).thenReturn(new ArrayList<>());
//
//        // When
//        ResponseEntity<AfterLogInResponseDto> responseEntity = mainService.getHomePageSource(users);
//
//        // Then
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        // 적절한 응답 검증을 수행
//    }
}