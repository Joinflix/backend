package com.sesac.joinflix.domain.chat.dto.request;

public record VoiceSignalRequest(
        String type,      // "offer", "answer", "candidate", or "mute-status"
        String sdp,       // Session description
        Object candidate, // ICE candidate info
        String sender,    // Nickname of sender
        String target,    // Nickname of who should receive this
        Boolean isMuted   // For mic toggle sync
) {}
