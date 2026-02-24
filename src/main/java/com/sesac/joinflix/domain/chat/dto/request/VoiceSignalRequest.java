package com.sesac.joinflix.domain.chat.dto.request;

import com.sesac.joinflix.domain.chat.dto.SignalType;

public record VoiceSignalRequest(
        SignalType type,      // "JOIN", "OFFER", "ANSWER", "ICE", or "MUTE_STATUS"
        String sdp,       // Session description
        IceCandidateRequest candidate, // ICE candidate info
        Long senderId,
        String senderNickname,    // Nickname of sender
        Long targetId,
        String targetNickname,    // Nickname of who should receive this
        Boolean isMuted   // For mic toggle sync
) {}
