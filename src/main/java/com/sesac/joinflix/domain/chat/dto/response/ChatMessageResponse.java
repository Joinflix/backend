package com.sesac.joinflix.domain.chat.dto.response;

import com.sesac.joinflix.domain.chat.dto.MessageType;

public record ChatMessageResponse(
    MessageType messageType,
    String sender,
    String message,
    Integer currentCount
) {

    public static ChatMessageResponse enter(String sender, Integer currentCount) {
        return new ChatMessageResponse(MessageType.ENTER, sender, sender + "님이 입장하셨습니다.",
            currentCount);
    }

    public static ChatMessageResponse talk(String sender, String message, Integer currentCount) {
        return new ChatMessageResponse(MessageType.TALK, sender, message, currentCount);
    }

    public static ChatMessageResponse leave(String sender, Integer currentCount) {
        return new ChatMessageResponse(MessageType.LEAVE, sender, sender + "님이 퇴장하셨습니다.",
            currentCount);
    }
}
