package com.back.moment.users.controller;

import com.back.moment.oauth.KakaoService;
import com.back.moment.users.dto.LoginRequestDto;
import com.back.moment.users.dto.SignupRequestDto;
import com.back.moment.users.jwt.JwtUtil;
import com.back.moment.users.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping(value = "/signup", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signup(@Valid @RequestPart(value = "signup") SignupRequestDto requestDto,
                                       @RequestPart(value = "profile", required = false) MultipartFile profileImg,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                stringBuilder.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return userService.signup(requestDto, profileImg);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

    @GetMapping("/kakao")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        ResponseEntity<Void> result = kakaoService.kakaoLogin(code, response);
        
        String createToken = response.getHeader(JwtUtil.ACCESS_KEY);
        Cookie cookie = new Cookie(JwtUtil.ACCESS_KEY, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/main"; //주소 요청으로 변경
    }
}
