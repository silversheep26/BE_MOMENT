package com.back.moment.users.service;

import com.back.moment.exception.ApiException;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.dto.SignupRequestDto;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

//import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

//    @AfterEach
//    void tearDown() {
//        usersRepository.deleteAllInBatch();
//    }



    @DisplayName("이메일 신규 회원 가입")
    @Test
    void signupTest() throws IOException {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test14@example.com", "password123+", "nickname14", "FEMALE", "MODEL");
        MockMultipartFile profileImg = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        when(s3Uploader.upload(profileImg)).thenReturn("이미지 경로");
        when(usersRepository.findByEmail("test14@example.com")).thenReturn(Optional.empty());
        when(usersRepository.findByNickName("nickname14")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123+")).thenReturn("가나다라마바사");

        // when
        ResponseEntity<Void> response = userService.signup(signupRequestDto, profileImg);

        // then
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
    }

    @DisplayName("중복된 닉네임으로 회원 가입")
    @Test
    void signupDuplicatedNickNameTest() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test14@example.com", "password123+", "nickname14", "FEMALE", "MODEL");
        MockMultipartFile profileImg = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        when(usersRepository.findByNickName("nickname14")).thenReturn(Optional.of(new Users()));

        // when

        // then
        assertThatThrownBy(() -> userService.signup(signupRequestDto, profileImg))
        .isInstanceOf(ApiException.class);

    }

    @DisplayName("중복된 이메일로 회원 가입")
    @Test
    void signupDuplicatedEmailTest() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test14@example.com", "password123+", "nickname14", "FEMALE", "MODEL");
        MockMultipartFile profileImg = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        when(usersRepository.findByEmail("test14@example.com")).thenReturn(Optional.of(new Users()));

        // when

        // then
        assertThatThrownBy(() -> userService.signup(signupRequestDto, profileImg))
                .isInstanceOf(ApiException.class);
    }

    @DisplayName("프로필 사진 없이 회원 가입")
    @Test
    void signupWithoutProfileImgTest() throws IOException {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test14@example.com", "password123+", "nickname14", "FEMALE", "MODEL");
        MockMultipartFile profileImg = null;
        String defaultProfileImgUrl = "https://moment-photo-resized.s3.ap-northeast-2.amazonaws.com/%EC%97%AC%EC%9E%90.jpg";
        Mockito.lenient().when(s3Uploader.upload(isNull())).thenReturn(defaultProfileImgUrl);
        when(usersRepository.findByEmail("test14@example.com")).thenReturn(Optional.empty());
        when(usersRepository.findByNickName("nickname14")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123+")).thenReturn("가나다라마바사");

        // when
        ResponseEntity<Void> response = userService.signup(signupRequestDto, profileImg);

        // then
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());

        // lenient().when 사용시 불필요한 스터빙을 무히하는 lenient 모드활성화
    }




    @Test
    void login() {
    }

    @Test
    void deleteUsersHard() {
    }

}