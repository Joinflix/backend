package com.sesac.joinflex.domain.userhistory.entity;

import com.sesac.joinflex.domain.user.entity.SocialProviderType;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAction action; // SIGNUP, LOGIN, SOCIAL_LINK, PWD_CHG 등

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "is_success", nullable = false)
    private boolean isSuccess;

    private String userAgent; // 접속 브라우저/기기 정보

    private String sessionId; // Redis 세션과 매칭 (동시 접속 제어용)

    private String details; // "카카오 연동 성공", "비밀번호 5회 오류" 등 부가 정보

    @Enumerated(EnumType.STRING)
    private SocialProviderType loginType; // 로그인 시 어떤 수단이었는지 기록

    @Builder
    private UserHistory(String email, UserAction action, String ip, boolean isSuccess, String userAgent, String sessionId, String details, SocialProviderType loginType) {
        this.email = email;
        this.action = action;
        this.ip = ip;
        this.isSuccess = isSuccess;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.details = details;
        this.loginType = loginType;
    }
}
