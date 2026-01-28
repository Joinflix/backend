package com.sesac.joinflex.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Redis 내부에 리스트 형태로 저장될 세션 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {
    private String refreshToken; // 리프레시 토큰
    private String sessionId;    // 기기 별 세션 ID
}
