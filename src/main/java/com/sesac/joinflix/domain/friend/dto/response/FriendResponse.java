package com.sesac.joinflix.domain.friend.dto.response;

import com.sesac.joinflix.domain.user.entity.User;

public record FriendResponse(Long userId, String nickname, String email) {
    public static FriendResponse from(User user) {
        return new FriendResponse(
            user.getId(),
            user.getNickname(),
            user.getEmail()
        );
    }
}
