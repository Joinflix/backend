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

    // Auth & User
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    LOCKED_ACCOUNT(HttpStatus.FORBIDDEN, "잠긴 계정입니다. 관리자에게 문의하세요."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "본인만 수정 및 조회가 가능합니다."),
    TOO_MANY_REGISTRATION_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "해당 IP에서 단시간에 너무 많은 가입 시도가 발생했습니다."),
    TOO_MANY_LOGIN_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "로그인 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    SOCIAL_LOGIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 처리 중 오류가 발생했습니다."),
    SOCIAL_ALREADY_LINKED(HttpStatus.BAD_REQUEST, "이미 다른 소셜 계정과 연동된 이메일입니다."),

    // Email Verification
    EMAIL_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일 인증 정보가 없거나 이미 처리되었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.GONE, "인증 번호가 만료되었습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증 번호가 일치하지 않습니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생했습니다."),
    TOO_MANY_VERIFICATION_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "인증 시도 5회를 초과했습니다."),

    // Profile Image (기존과 동일)
    IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 이미지 파일이 선택되지 않았습니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "이미지 크기는 최대 10MB를 초과할 수 없습니다."),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "이미지는 jpg, jpeg, png, gif, webp 형식만 가능합니다."),
    IMAGE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 사진 저장 중 서버 오류가 발생했습니다."),

    // Token
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않거나 만료된 리프레시 토큰입니다."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "다른 기기에서 로그인하여 세션이 만료되었습니다."),

    // Membership
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 멤버십입니다."),
    MEMBERSHIP_EXPIRED(HttpStatus.FORBIDDEN, "멤버십 기간이 만료되었습니다."),

    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다. 위변조가 의심됩니다."),
    PAYMENT_NOT_PAID(HttpStatus.BAD_REQUEST, "결제가 완료되지 않은 상태입니다."),
    PAYMENT_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 검증 중 오류가 발생했습니다."),
    ALREADY_PAID(HttpStatus.CONFLICT, "이미 처리가 완료된 결제 건입니다."),
    JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터를 JSON으로 변환하는 데 실패했습니다."),

    //REFUND
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 주문입니다."),
    REFUND_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 환불 요청입니다."),
    REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "환불 처리에 실패했습니다. 잠시 후 다시 시도해 주세요."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "환불 권한이 없습니다."),
    INVALID_REFUND_STATUS(HttpStatus.BAD_REQUEST, "결제 완료 상태가 아니어서 환불을 처리할 수 없습니다."),

    // Movie
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 영화입니다."),

    // Party
    INVALID_PARTY_INVITE_TARGET(HttpStatus.BAD_REQUEST, "친구가 아니거나 존재하지 않는 사용자는 파티에 초대할 수 없습니다.");



    private final HttpStatus status;
    private final String message;
}
