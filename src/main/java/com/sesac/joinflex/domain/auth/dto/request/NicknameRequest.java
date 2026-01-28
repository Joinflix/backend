package com.sesac.joinflex.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameRequest {
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 3, max = 30, message = "닉네임은 3~30자여야 합니다")
    private String nickname;
}
