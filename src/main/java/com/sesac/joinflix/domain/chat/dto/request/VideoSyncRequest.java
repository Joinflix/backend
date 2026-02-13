package com.sesac.joinflix.domain.chat.dto.request;

public record VideoSyncRequest(
    Double currentTime,
    Boolean paused
) {
}
