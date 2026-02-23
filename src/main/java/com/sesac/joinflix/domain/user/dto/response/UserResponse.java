package com.sesac.joinflix.domain.user.dto.response;

import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.domain.user.entity.UserRoleType;
import com.sesac.joinflix.domain.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private UserRoleType role;
    private String profileImageUrl;
    private UserStatus status;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRoleType())
                .profileImageUrl(user.getProfileImageUrl())
                .status(user.getStatus())
                .build();
    }
}
