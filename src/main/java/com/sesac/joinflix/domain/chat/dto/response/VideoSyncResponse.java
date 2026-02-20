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

        String formattedTime = formatTime(currentTime);

        String message = switch (action) {
            case PLAY -> sender + "님이 재생했습니다.";
            case PAUSE -> sender + "님이 일시정지했습니다.";
            case SEEK -> sender + "님이 " + formattedTime + "으로 이동했습니다.";
        };

        return new VideoSyncResponse(MessageType.SYSTEM, sender, currentTime, paused, message);
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
