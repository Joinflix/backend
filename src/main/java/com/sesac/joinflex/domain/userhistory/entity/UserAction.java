package com.sesac.joinflex.domain.userhistory.entity;

public enum UserAction {
    // account
    EMAIL_AUTH,     // 이메일 인증
    SIGNUP,         // 회원가입
    LOGIN,          // 로그인
    LOGOUT,         // 로그아웃
    SOCIAL_LINK,    // 소셜 연동 (isSocial: false -> true)
    PWD_CHG,        // 비밀번호 변경
    NICKNAME_CHK,   // 닉네임 중복 확인
    NICKNAME_CHG,   // 닉네임 변경
    ACCOUNT_LOCK,   // 계정 잠금
    ACCOUNT_UNLOCK, // 계정 해제

    // payment & membership
    PAYMENT_READY,           // 결제 시작 (주문 생성)
    PAYMENT_SUCCESS,         // 결제 최종 완료 (검증 성공)
    PAYMENT_FAIL,            // 결제 실패 (금액 불일치 등)
    REFUND_SUCCESS,          // 환불 성공
    REFUND_FAIL,             // 환불 실패
    LOGIN_EXPIRED_MEMBERSHIP, // 멤버십 만료 상태로 로그인
    MEMBERSHIP_UPGRADE,      // 멤버십 등급 변경 완료
    MEMBERSHIP_AUTO_EXPIRED,  // 스케줄러에 의한 자동 만료
    MEMBERSHIP_EXTEND,        // 멤버십 연장(추가 결제)

    // session & security
    SESSION_FORCED_LOGOUT,   // 동시 접속 제한으로 인해 기존 세션 밀려남 (중요!)
    UNAUTHORIZED_ACCESS      // 권한 없는 페이지/기능 접근 시도
}
