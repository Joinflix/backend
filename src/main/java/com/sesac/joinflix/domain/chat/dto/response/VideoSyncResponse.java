package com.sesac.joinflix.domain.chat.dto.response;

import com.sesac.joinflix.domain.chat.dto.Action;
import com.sesac.joinflix.domain.chat.dto.MessageType;

public record VideoSyncResponse(
    MessageType messageType,
    String senderNickname,
    Double currentTime,
    Boolean paused,
    String message,
    Long senderId,
    Action action
) {

    public static VideoSyncResponse of(String senderNickname, Double currentTime, Boolean paused,
        Action action, Long senderId) {

        String formattedTime = formatTime(currentTime);

        String message = switch (action) {
            case PLAY -> senderNickname + "님이 재생했습니다.";
            case PAUSE -> senderNickname + "님이 일시정지했습니다.";
            case SEEK -> senderNickname + "님이 " + formattedTime + "으로 이동했습니다.";
        };

        return new VideoSyncResponse(MessageType.SYSTEM, senderNickname, currentTime, paused, message, senderId, action);
    }

    private static String formatTime(Double seconds) {
        if (seconds == null) return "00:00";

        int totalSeconds = (int) Math.round(seconds);

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        }
        return String.format("%d:%02d", minutes, secs);
    }

}
