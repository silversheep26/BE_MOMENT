package com.back.moment.users.service;

import static com.back.moment.users.jwt.JwtUtil.ACCESS_KEY;
import static com.back.moment.users.jwt.JwtUtil.REFRESH_KEY;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.global.service.RedisService;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.dto.*;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.jwt.JwtUtil;
import com.back.moment.users.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.secret.key}")
    private String secretKey; // 암호화/복호화에 필요

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    public static final String BEARER_PREFIX = "Bearer ";
    private final S3Uploader s3Uploader;
    private final JwtUtil jwtUtil;
    //    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;

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
        String gender = requestDto.getGender();
        RoleEnum role = requestDto.getRole();

        users.saveUsers(requestDto, password, gender, role);

        // 프로필 이미지 처리
        if (profileImg != null) {
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

    @Transactional
    public ResponseEntity<UserInfoResponseDto> login(LoginRequestDto loginRequestDto,
        HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        try {
            Users users = usersRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("없는 이메일 입니다.")
            );
            if (!passwordEncoder.matches(password, users.getPassword())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            jwtUtil.init();
            // 토큰에 모델인지 작가인지 판단하는 role 입력
            TokenDto tokenDto = jwtUtil.createAllToken(users, users.getRole());

//            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(loginRequestDto.getEmail());
//
//            if (refreshToken.isPresent()) {
//                RefreshToken savedRefreshToken = refreshToken.get();
//                RefreshToken updateToken = savedRefreshToken.updateToken(tokenDto.getRefreshToken().substring(7));
//                refreshTokenRepository.save(updateToken);
//            } else {
//                RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), email);
//                refreshTokenRepository.save(newToken);
//            }

            String redisKey = tokenDto.getRefreshToken().substring(7);
            String refreshRedis = redisService.getValues(redisKey);
            if (refreshRedis == null) {
                redisService.setRefreshValues(redisKey, users.getEmail());
            }

            Claims claim = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(tokenDto.getAccessToken().substring(7)).getBody();
            Long userId = claim.get("userId", Long.class);
            String nickName = claim.get("nickName", String.class);
            String profileImg = claim.get("profileImg", String.class);
            String role = claim.get("role", String.class);
            UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(userId, nickName,
                profileImg, role);
            //응답 헤더에 토큰 추가
            setHeader(response, tokenDto);

            return ResponseEntity.ok(userInfoResponseDto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @Transactional(readOnly = true)
//    public ResponseEntity<Void> logout(HttpServletRequest request){
//        String refreshToken = request.getHeader(REFRESH_KEY).substring(7);
//
//        if(!refreshToken.isEmpty() && redisService.getValues(refreshToken) != null){
//            redisService.deleteValues(refreshToken);
//        } else {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return ResponseEntity.ok(null);
//    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(REFRESH_KEY, tokenDto.getRefreshToken());
    }
    
}
