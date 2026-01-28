package com.sesac.joinflex.domain.user.dto.response;

import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Membership membership;

    public static UserProfileResponse of(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .membership(user.getMembership())
                .build();
    }
}
