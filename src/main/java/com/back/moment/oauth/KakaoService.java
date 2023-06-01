package com.back.moment.oauth;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.global.service.RedisService;
import com.back.moment.users.dto.KakaoUserInfoDto;
import com.back.moment.users.dto.TokenDto;
//import com.back.moment.users.entity.RefreshToken;
import com.back.moment.users.dto.UserInfoResponseDto;
import com.back.moment.users.entity.GenderEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.jwt.JwtUtil;
//import com.back.moment.users.repository.RefreshTokenRepository;
import com.back.moment.users.repository.UsersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    @Value("${jwt.secret.key}")
    private String secretKey; // 암호화/복호화에 필요
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    @Value("${kakao.client.id}")
    private String client_id;

    @Value("${kakao.client.secret}")
    private String client_secret;
    @Value("${kakao.redirect.uri}")
    private String redirect_uri;


    public ResponseEntity<UserInfoResponseDto> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);


        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        Users kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        //Token 생성
        TokenDto tokenDto = jwtUtil.createAllToken(kakaoUser, kakaoUser.getRole());
        //RefreshToken 있는지 확인
//        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(kakaoUser.getEmail());

        // 있으면 새 토큰 발급 후 업데이트
        // 없으면 새로 만들고 DB에 저장
//        if (refreshToken.isPresent()) {
//            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
//        } else {
//            refreshTokenRepository.saveAndFlush(new RefreshToken(tokenDto.getRefreshToken(), kakaoUser.getEmail()));
//        }

        String redisKey = tokenDto.getRefreshToken().substring(7);
        String refreshRedis = redisService.getValues(redisKey);
        if(refreshRedis == null){
            redisService.setValues(redisKey, kakaoUser.getEmail());
        }

        Claims claim = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenDto.getAccessToken().substring(7)).getBody();
        Long userId = claim.get("userId", Long.class);
        String nickName = claim.get("nickName", String.class);
        String profileImg = claim.get("profileImg", String.class);
        String role = claim.get("role", String.class);
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(userId, nickName, profileImg, role);
        //응답 헤더에 토큰 추가
        setHeader(response, tokenDto);

        return ResponseEntity.ok(userInfoResponseDto);
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("client_secret",client_secret);
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);
        System.out.println("code = "+code);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                kakaoTokenRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ExceptionEnum.FAIL_LOGIN);
        }
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ExceptionEnum.FAIL_LOGIN);
        }

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String gender = jsonNode.get("kakao_account")
                .get("gender").asText();
        String profileImg = jsonNode.get("properties")
                .get("profile_image").asText();
        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, email, nickname, gender, profileImg);
    }

    // 3. 필요시에 회원가입
    private Users registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Users kakaoUser = usersRepository.findByKakaoId(kakaoId)
                .orElse(null);
        Users users = new Users();
        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            Users sameEmailUser = usersRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoUserInfo.getEmail();
                String gender = "MALE";
                if(kakaoUserInfo.getGender().equals("female"))
                    gender = "FEMALE";



                kakaoUser = new Users(email, kakaoUserInfo.getNickName(),encodedPassword, gender, kakaoUserInfo.getProfileImg());
            }

            usersRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(REFRESH_KEY, tokenDto.getRefreshToken());
    }
}