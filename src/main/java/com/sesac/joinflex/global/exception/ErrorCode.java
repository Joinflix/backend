package com.sesac.joinflex.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다"),

    // Friend
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."),
    FRIEND_REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 친구 요청이 이미 존재합니다."),
    FRIEND_REQUEST_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    FRIEND_REQUEST_INVALID_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 친구 요청 상태입니다."),
    FRIEND_REQUEST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 친구 요청에 대한 권한이 없습니다.");


    private final HttpStatus status;
    private final String message;
}
