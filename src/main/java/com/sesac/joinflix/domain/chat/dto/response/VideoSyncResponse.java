package com.sesac.joinflix.domain.chat.dto.response;

import com.sesac.joinflix.domain.chat.dto.Action;
import com.sesac.joinflix.domain.chat.dto.MessageType;

public record VideoSyncResponse(
    MessageType messageType,
    String sender,
    Double currentTime,
    Boolean paused,
    String message
) {

    public static VideoSyncResponse of(String sender, Double currentTime, Boolean paused,
        Action action) {
        String message = switch (action) {
            case PLAY -> sender + "님이 재생했습니다.";
            case PAUSE -> sender + "님이 일시정지했습니다.";
            case SEEK -> sender + "님이 " + currentTime + "으로 이동했습니다.";
        };

        return new VideoSyncResponse(MessageType.SYSTEM, sender, currentTime, paused, message);
    }

}
