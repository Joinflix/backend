package com.sesac.joinflex.domain.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {

    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String nickname;

    private String profileImageUrl;
}
