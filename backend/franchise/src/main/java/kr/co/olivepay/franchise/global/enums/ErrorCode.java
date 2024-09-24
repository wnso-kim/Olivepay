package kr.co.olivepay.franchise.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResponseCode {

    // API

    // Common Error Code
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부적 에러가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 요청을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메소드 입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다."),

    //Franchise Error Code
    FRANCHISE_REGISTRATION_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "이미 가맹점으로 등록되어 있는 가게 입니다."),
    QR_CREATE_ERROR(HttpStatus.CONFLICT, "QR 코드 생성 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
