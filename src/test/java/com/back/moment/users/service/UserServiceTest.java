package com.back.moment.users.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.global.service.RedisService;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.dto.LoginRequestDto;
import com.back.moment.users.dto.SignupRequestDto;
import com.back.moment.users.dto.TokenDto;
import com.back.moment.users.dto.UserInfoResponseDto;
import com.back.moment.users.entity.Users;
import com.back.moment.users.jwt.JwtUtil;
import com.back.moment.users.repository.UsersRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
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

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtParser jwtParser;

    @Mock
    private Jws<Claims> jws;

    @Value("${jwt.secret.key}")
    private String secretKey; // 암호화/복호화에 필요


    @DisplayName("이메일 신규 회원 가입 성공")
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

    @DisplayName("이메일 없이 회원 가입")
    @Test
    void signupWithoutEmailTest() throws IOException {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto(null, "password123+", "nickname14", "FEMALE", "MODEL");
        MockMultipartFile profileImg = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // when
        ResponseEntity<Void> response = userService.signup(signupRequestDto, profileImg);

        // then
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(response.getStatusCode());

    }

    @DisplayName("로그인 성공 케이스")
    @Test
    void loginSuccess() {
        //given
        String email = "test@example.com";
        String password = "password";

        Users user = new Users("test@example.com", "nickName", "password", "FEMALE", "MODEL");
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        TokenDto mockedTokenDto = new TokenDto("mockedAccessToken", "mockedRefreshToken");

        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createAllToken(user, user.getRole())).thenReturn(mockedTokenDto);
        when(redisService.getRefreshToken(user.getEmail())).thenReturn(null);

        // when
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        ResponseEntity<UserInfoResponseDto> response = userService.login(loginRequestDto, mockedResponse);

        // then
        assertThat(user.getEmail()).isEqualTo(loginRequestDto.getEmail());
        assertThat(user.getPassword()).isEqualTo(loginRequestDto.getPassword());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
    @Test
    void deleteUsersHard() {
    }




}