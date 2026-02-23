package com.sesac.joinflix.domain.chat.dto.response;

import com.sesac.joinflix.domain.chat.dto.MessageType;

public record ChatMessageResponse(
    MessageType messageType,
    String senderNickname,
    String message,
    Integer currentCount,
    Long senderId
) {

    public static ChatMessageResponse enter(String senderNickname, Integer currentCount, Long senderId) {
        return new ChatMessageResponse(MessageType.ENTER, senderNickname, senderNickname + "님이 입장하셨습니다.",
            currentCount, senderId);
    }

    public static ChatMessageResponse talk(String senderNickname, String message, Integer currentCount, Long senderId) {
        return new ChatMessageResponse(MessageType.TALK, senderNickname, message, currentCount, senderId);
    }

    public static ChatMessageResponse leave(String senderNickname, Integer currentCount, Long senderId) {
        return new ChatMessageResponse(MessageType.LEAVE, senderNickname, senderNickname + "님이 퇴장하셨습니다.",
            currentCount, senderId);
    }
}
