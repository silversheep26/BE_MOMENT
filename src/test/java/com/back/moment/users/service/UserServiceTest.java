package com.back.moment.users.service;

import com.back.moment.exception.ApiException;
import com.back.moment.global.service.RedisService;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.dto.LoginRequestDto;
import com.back.moment.users.dto.SignupRequestDto;
import com.back.moment.users.dto.TokenDto;
import com.back.moment.users.dto.UserInfoResponseDto;
import com.back.moment.users.entity.Users;
import com.back.moment.users.jwt.JwtUtil;
import com.back.moment.users.repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisService redisService;

//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//    }

//    @AfterEach
//    void tearDown() {
//        usersRepository.deleteAllInBatch();
//    }



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


    @Test
    @DisplayName("로그인 성공")
    void loginTest() {

        // given
        // 유저 정보
        String email = "test@example.com";
        String password = "password";

        // Create a LoginRequestDto
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        // 가상의 유저 생성
//        Users user = new Users("test@test.com", "password", "nickname14", "FEMALE", "MODEL");
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setNickName("nick");
        user.setProfileImg("img.jpg");
        user.setRole("MODEL");
        user.setGender("FEMALE");

        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(true);  // 올바른 비밀번호 비교 결과 설정

        TokenDto tokenDto = new TokenDto("Bearer <access_token>", "refreshToken");
//        when(jwtUtil.createAllToken(user, user.getRole())).thenReturn(tokenDto);
        when(jwtUtil.createAllToken(any(), anyString())).thenReturn(tokenDto);
        when(redisService.getRefreshToken(user.getEmail())).thenReturn(null);
        when(jwtUtil.getUserInfoFromToken(anyString())).thenReturn(email);

        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(user.getId(), user.getNickName(), user.getProfileImg(), user.getRole());

        // when
        // 모의 객체(Mock)를 사용하여 테스트
        HttpServletResponse mockresponse = mock(HttpServletResponse.class);
        System.out.println(mockresponse);

        // 로그인 요청을 서비스에 전달하고, 응답을 받아온 뒤에 해당 응답을 테스트
        ResponseEntity<UserInfoResponseDto> responseEntity = userService.login(loginRequestDto, mockresponse);
        System.out.println(loginRequestDto.getEmail() + ", " + loginRequestDto.getPassword());
        System.out.println(responseEntity);

        // then
        // 예상되는 응답 코드와 결과를 테스트
        verify(usersRepository).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder).matches(loginRequestDto.getPassword(), user.getPassword());
        verify(jwtUtil).createAllToken(user, user.getRole());
        verify(redisService).getRefreshToken(user.getEmail());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 예시: usersRepository.findByEmail 메서드가 정확히 한 번 호출되었는지 확인
//        verify(usersRepository, times(1)).findByEmail(loginRequestDto.getEmail());


    }

    @DisplayName("로그인 성공")
    @Test
    public void testLogin_Success() {
        // Mocked 객체 생성
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");
        Users mockedUser = new Users();
        mockedUser.setId(1L);
        mockedUser.setEmail("test@test.com");
        mockedUser.setPassword(passwordEncoder.encode("password"));
        mockedUser.setNickName("nick");
        mockedUser.setProfileImg("img.jpg");
        mockedUser.setRole("MODEL");
        mockedUser.setGender("FEMALE");

        TokenDto mockedTokenDto = new TokenDto("mockedAccessToken", "mockedRefreshToken");

        // Mock 설정
        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(mockedUser));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), mockedUser.getPassword())).thenReturn(true);
        when(jwtUtil.createAllToken(mockedUser, mockedUser.getRole())).thenReturn(mockedTokenDto);
        doNothing().when(jwtUtil).init();
        when(redisService.getRefreshToken(mockedUser.getEmail())).thenReturn(null);
        doNothing().when(redisService).setRefreshValues(mockedUser.getEmail(), mockedTokenDto.getRefreshToken().substring(7));


        // 테스트 실행
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        ResponseEntity<UserInfoResponseDto> response = userService.login(loginRequestDto, mockedResponse);

        // 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // 기타 검증 로직 추가

        // 예상되는 메서드 호출을 확인하기 위한 Mockito 검증
        verify(usersRepository).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder).matches(loginRequestDto.getPassword(), mockedUser.getPassword());
        verify(jwtUtil).createAllToken(mockedUser, mockedUser.getRole());
        verify(redisService).getRefreshToken(mockedUser.getEmail());
        verify(redisService).setRefreshValues(mockedUser.getEmail(), mockedTokenDto.getRefreshToken().substring(7));
        // 기타 예상된 메서드 호출 검증 추가
    }


    @Test
    void deleteUsersHard() {
    }

}