package kr.co.olivepay.franchise.global.handler;

import kr.co.olivepay.franchise.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
