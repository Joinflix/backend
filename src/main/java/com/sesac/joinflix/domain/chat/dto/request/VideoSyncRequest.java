package com.sesac.joinflix.domain.chat.dto.request;

import com.sesac.joinflix.domain.chat.dto.Action;

public record VideoSyncRequest(
    Double currentTime,
    Boolean paused,
    Action action
) {
}
