package com.sesac.joinflix.domain.friend.dto.request;
import jakarta.validation.constraints.NotNull;

public record FriendRequestCreateRequest(@NotNull(message = "receiverId는 필수입니다.") Long receiverId) {
}
