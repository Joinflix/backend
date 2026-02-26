package com.sesac.joinflix.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String status; // "FRIEND", "PENDING", "REQUESTED", "NONE"
    private Long requestId;
    private Integer priority; // 1:신청받음, 2:대기중, 3:친구, 4:생판남
}