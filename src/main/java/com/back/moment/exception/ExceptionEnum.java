package com.back.moment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    // 400 Bad_Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "아이디 또는 비밀번호가 일치하지 않습니다."),

    PASSWORD_REGEX(HttpStatus.BAD_REQUEST, "400_1", "비밀번호는 8~15자리, a-z, A-Z, 숫자, 특수문자 조합으로 구성되어야 합니다."),

    NOT_MATCH_TOKEN(HttpStatus.BAD_REQUEST,"400_2", "토큰값이 일치하지 않습니다."),

    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST,"400_3","전달 값이 잘못 되었습니다"),
    NOT_MATCH_USERS(HttpStatus.BAD_REQUEST,"400_4","유저가 일치하지 않습니다."),
    FAIL_LOGIN(HttpStatus.BAD_REQUEST, "400_5", "로그인에 실패하였습니다."),
    FAIL_MAIL_SEND(HttpStatus.BAD_REQUEST,"400_6","메일 전송에 실패하였습니다."),
    EXIST_MAIL(HttpStatus.BAD_REQUEST,"400_7","이미 존재하는 메일입니다."),
    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "권한이 없습니다."),
    LOGIN(HttpStatus.UNAUTHORIZED, "401_1", "로그인 후 이용가능합니다."),

    // 404 Not Found
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "404_1", "게시글이 존재하지 않습니다."),
    NOT_FOUND_PHOTO(HttpStatus.NOT_FOUND, "404_2", "사진이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404_3", "사용자가 존재하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "404_4", "리프레시 토큰이 없습니다."),

    // 409 Conflict
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "409", "중복된 이메일이 존재합니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "409", "중복된 닉네임이 존재합니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버에러");

    private final HttpStatus status;
    private final String code;
    private final String detailMsg;
}
