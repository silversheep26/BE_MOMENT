package com.back.moment.exception;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {

    private String message;
    private int status;
    private String detail;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ExceptionEnum e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.builder()
                        .message(e.getDetailMsg())
                        .status(e.getStatus().value())
                        .build());
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus httpStatus, String detail) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .message(httpStatus.name())
                        .status(httpStatus.value())
                        .detail(detail)
                        .build());
    }
}
