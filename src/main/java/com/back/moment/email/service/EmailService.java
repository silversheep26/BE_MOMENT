package com.back.moment.email.service;


import com.back.moment.email.dto.CodeRequestDto;
import com.back.moment.email.dto.EmailRequestDto;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.global.service.RedisService;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

@PropertySource("classpath:application-secret.yml")
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class EmailService {

    private final JavaMailSender javaMailSender;
//    private final EmailRepository emailRepository;
    private final UsersRepository usersRepository;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String id;

    public MimeMessage createMessage(String to, String code) throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : " + to);
        log.info("인증 번호 : " + code);
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("Moment 회원가입 인증 코드: "); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String msg = "";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += code;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id, "Moment 고객센터")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((random.nextInt(10)));
        }
        return key.toString();
    }

    /*
        메일 발송
        sendSimpleMessage의 매개변수로 들어온 to는 인증번호를 받을 메일주소
        MimeMessage 객체 안에 내가 전송할 메일의 내용을 담아준다.
        bean으로 등록해둔 javaMailSender 객체를 사용하여 이메일 send
     */
    public ResponseEntity<Void> sendMessage(EmailRequestDto emailRequestDto) {
        Optional<Users> findEmail = usersRepository.findByEmail(emailRequestDto.getEmail());
        if(findEmail.isPresent()) throw new ApiException(ExceptionEnum.EXIST_MAIL);
        String code = createKey(); // 인증코드 생성
//        Email email = Email.saveEmail(emailRequestDto); // 이메일 객체 생성
        MimeMessage message = null;
        try {
            message = createMessage(emailRequestDto.getEmail(), code); // 전송 메시지 삭성 메서드 호출
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new ApiException(ExceptionEnum.FAIL_MAIL_SEND);
        }
        try {
            javaMailSender.send(message); // 메일 발송
        } catch (MailException es) {
            throw new ApiException(ExceptionEnum.FAIL_MAIL_SEND);
        }
        redisService.setCodeValues(emailRequestDto.getEmail(),code);

        log.info("인증 코드 : " + code);
        return ResponseEntity.ok(null);
    }

    /*
        DB에 Email테이블에서 인증코드와 일치하는지 판별,
        일치여부와 상관없이 해당 레코드는 무조건 삭제
     */
    public ResponseEntity<Void> codeCheck(CodeRequestDto codeRequestDto) {

        String redisCode = redisService.getCode(codeRequestDto.getEmail());
        if(redisCode == null) {
            throw new ApiException(ExceptionEnum.FAIL_MAIL_SEND);
        }
        if(!redisCode.equals(codeRequestDto.getCode()))
            throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
        redisService.deleteValues(codeRequestDto.getCode());

//        Email email = emailRepository.findById(codeRequestDto.getEmail()).orElseThrow(() -> new ApiException(ExceptionEnum.FAIL_MAIL_SEND));
//        emailRepository.deleteById(codeRequestDto.getEmail());
//        if (!email.getCode().equals(codeRequestDto.getCode())) {
//            throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
//        }
        return ResponseEntity.ok(null);
    }
}