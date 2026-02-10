package com.sesac.joinflex.domain.user.entity;

public enum UserStatus {
    PENDING,   // 가입 정보 입력 완료 (결제 전)
    ACTIVE,    // 결제 완료 (정식 회원)
    SUSPENDED, // 정지
    WITHDRAWN  // 탈퇴
}
