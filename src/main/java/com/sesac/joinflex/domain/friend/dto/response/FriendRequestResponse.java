package com.sesac.joinflex.domain.friend.dto.response;

import com.sesac.joinflex.domain.friend.entity.FriendRequest;
import com.sesac.joinflex.domain.friend.entity.FriendRequestStatus;

import java.time.LocalDateTime;

public record FriendRequestResponse(Long requestId, FriendRequestStatus status, Long senderId, Long receiverId, LocalDateTime createdAt) {
    public static FriendRequestResponse from(FriendRequest request) {
        return new FriendRequestResponse(
            request.getId(),
            request.getStatus(),
            request.getSender().getId(),
            request.getReceiver().getId(),
            request.getCreatedAt()
        );
    }
}
