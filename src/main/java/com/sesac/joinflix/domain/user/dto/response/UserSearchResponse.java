package com.sesac.joinflix.domain.user.dto.response;

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
    private Long requestId;
}