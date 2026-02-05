package com.sesac.joinflex.domain.chat.dto.response;

import com.sesac.joinflex.domain.chat.dto.MessageType;

public record ChatMessageResponse(
    MessageType messageType,
    String sender,
    String message
) {
}
