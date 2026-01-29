package com.sesac.joinflex.domain.user.dto.response;

import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.entity.UserRoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickName;
    private UserRoleType role;
    private String profileImageUrl;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickname())
                .role(user.getRoleType())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
