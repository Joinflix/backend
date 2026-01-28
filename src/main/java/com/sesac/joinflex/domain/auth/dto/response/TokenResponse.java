package com.sesac.joinflex.domain.auth.dto.response;

import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse userResponse;
}
