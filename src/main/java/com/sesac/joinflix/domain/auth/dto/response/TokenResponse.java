package com.sesac.joinflix.domain.auth.dto.response;

import com.sesac.joinflix.domain.user.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse userResponse;
}
