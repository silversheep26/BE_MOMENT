package com.back.moment.users.controller;

import com.back.moment.email.dto.CodeRequestDto;
import com.back.moment.email.dto.EmailRequestDto;
import com.back.moment.email.service.EmailService;
import com.back.moment.oauth.KakaoService;
import com.back.moment.users.dto.LoginRequestDto;
import com.back.moment.users.dto.SignupRequestDto;
import com.back.moment.users.dto.UserInfoResponseDto;
import com.back.moment.users.security.UserDetailsImpl;
import com.back.moment.users.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final EmailService emailService;

    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signup(
        @Valid @RequestPart(value = "signup") SignupRequestDto requestDto,
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
    public ResponseEntity<UserInfoResponseDto> login(@RequestBody LoginRequestDto loginRequestDto,
        HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

    @GetMapping("/kakao")
    public ResponseEntity<UserInfoResponseDto> kakaoLogin(@RequestParam String code,
        HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @PostMapping("/kakao/role") //?role=MODEL
    public ResponseEntity<Void> kakaoRole(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam String role) {
        return kakaoService.saveRole(userDetails.getUsers(), role);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/main"; //주소 요청으로 변경
    }

//    // 회원 탈퇴 soft
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<Void> deleteUsers(@PathVariable Long userId,
//        @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return userService.deleteUsers(userId, userDetails.getUsers());
//    }

    // 회원 탈퇴 hard (영구 삭제)
    @PostMapping("/hard")
    public ResponseEntity<Void> deleteUsersHard(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        EmailRequestDto emailRequestDto = new EmailRequestDto(userDetails.getUsers().getEmail());
        return emailService.sendMessage(emailRequestDto);
    }
    @PostMapping("/code")
    public ResponseEntity<Void> checkCodeForDeleteUsers(String code,@AuthenticationPrincipal UserDetailsImpl userDetails){
        CodeRequestDto codeRequestDto = new CodeRequestDto(userDetails.getUsers().getEmail(),code);
        return emailService.codeCheck(codeRequestDto);
    }

}
