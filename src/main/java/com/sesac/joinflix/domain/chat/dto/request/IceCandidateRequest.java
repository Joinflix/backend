package com.sesac.joinflix.domain.chat.dto.request;

public record IceCandidateRequest(
        String candidate,
        String sdpMid,
        Integer sdpMLineIndex
) {
}
