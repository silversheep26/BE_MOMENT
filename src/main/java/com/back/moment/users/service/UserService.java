package com.back.moment.users.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.dto.LoginRequestDto;
import com.back.moment.users.dto.SignupRequestDto;
import com.back.moment.users.dto.TokenDto;
import com.back.moment.users.entity.RefreshToken;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.GenderEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.jwt.JwtUtil;
import com.back.moment.users.repository.RefreshTokenRepository;
import com.back.moment.users.repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.back.moment.users.jwt.JwtUtil.ACCESS_KEY;
import static com.back.moment.users.jwt.JwtUtil.REFRESH_KEY;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    public static final String BEARER_PREFIX = "Bearer ";
    private final S3Uploader s3Uploader;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ResponseEntity<Void> signup(SignupRequestDto requestDto, MultipartFile profileImg) {
        if (requestDto.getEmail() == null ||
            requestDto.getPassword() == null ||
            requestDto.getNickName() == null
            ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Users> findEmail = usersRepository.findByEmail(requestDto.getEmail());
        if (findEmail.isPresent()) {
            throw new ApiException(ExceptionEnum.DUPLICATED_USER_NAME);
        }
        Users users = new Users();
        String password = passwordEncoder.encode(requestDto.getPassword());
        GenderEnum gender = requestDto.getGender();
        RoleEnum role = requestDto.getRole();

        users.saveUsers(requestDto, password, gender, role);

        // 프로필 이미지 처리
        if(!profileImg.isEmpty()) {
            try {
                String imgPath = s3Uploader.upload(profileImg);
                users.setProfileImg(imgPath);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

//        if(!profileImg.isEmpty()) {
//            String imgPath = s3Uploader.upload(profileImg);
//            users.setProfileImg(imgPath);
//        }
        usersRepository.save(users);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        try {
            Users users = usersRepository.findByEmail(email).orElseThrow(
                    () -> new IllegalArgumentException("없는 이메일 입니다.")
            );
            if (!passwordEncoder.matches(password, users.getPassword())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 토큰에 모델인지 작가인지 판단하는 role 입력
            TokenDto tokenDto = jwtUtil.createAllToken(loginRequestDto.getEmail(), users.getRole());

            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(loginRequestDto.getEmail());

            if (refreshToken.isPresent()) {
                RefreshToken savedRefreshToken = refreshToken.get();
                RefreshToken updateToken = savedRefreshToken.updateToken(tokenDto.getRefreshToken().substring(7));
                refreshTokenRepository.save(updateToken);
            } else {
                RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), email);
                refreshTokenRepository.save(newToken);
            }

            //응답 헤더에 토큰 추가
            setHeader(response, tokenDto);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(REFRESH_KEY, tokenDto.getRefreshToken());
    }
}
