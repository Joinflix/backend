package com.sesac.joinflex.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponse {
    private Long id;
    private String email;
    private String nickName;
    private String profileImageUrl;
    private String friendStatus;
}