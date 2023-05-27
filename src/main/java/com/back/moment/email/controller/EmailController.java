package com.back.moment.email.controller;


import com.back.moment.email.dto.CodeRequestDto;
import com.back.moment.email.dto.EmailRequestDto;
import com.back.moment.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/emails")
@RestController
public class EmailController {

    private final EmailService emailService;

    /*
    가입시 메일을 작성하고 , 인증을 위해 해당 메일에
    인증코드를 보내는 컨트롤러
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/auth")
    public ResponseEntity<Void> mailSend(@RequestBody EmailRequestDto emailRequestDto) {
        return emailService.sendMessage(emailRequestDto);
    }

    /*
    가입시 작성한 메일에 날아온 인증코드를
    검증하는 컨트롤러
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/check")
    public ResponseEntity<Void> codeCheck(@RequestBody CodeRequestDto codeRequestDto) {
        return emailService.codeCheck(codeRequestDto);
    }
}