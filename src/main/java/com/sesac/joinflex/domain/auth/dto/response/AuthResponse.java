package com.sesac.joinflex.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON 응답에서 제외
public class AuthResponse {
    private String accessToken;
    private UserResponse user;

    public static AuthResponse from(String accessToken, UserResponse user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

    // 토큰 재발급 시
    public static AuthResponse from(String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
