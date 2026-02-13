package com.sesac.joinflix.domain.chat.dto.response;

import com.sesac.joinflix.domain.chat.dto.MessageType;

public record VideoSyncResponse(
    MessageType messageType,
    String sender,
    Double currentTime,
    Boolean paused
) {

    public static VideoSyncResponse of(String sender, Double currentTime, Boolean paused) {
        return new VideoSyncResponse(MessageType.SYSTEM, sender, currentTime, paused);
    }

}
