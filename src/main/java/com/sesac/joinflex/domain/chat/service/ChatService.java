package com.sesac.joinflex.domain.chat.service;

import com.sesac.joinflex.domain.chat.dto.MessageType;
import com.sesac.joinflex.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflex.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PartyRoomRepository partyRoomRepository;

    public ChatMessageResponse createEnterMessage(Long partyId, UserResponse user) {
        partyRoomRepository.findById(partyId).orElseThrow();
        return new ChatMessageResponse(MessageType.ENTER, user.getNickName(),
            user.getNickName() + "님이 입장하셨습니다.");
    }

    public ChatMessageResponse createTalkMessage(Long partyId, UserResponse user, String message) {
        partyRoomRepository.findById(partyId).orElseThrow();
        return new ChatMessageResponse(MessageType.TALK, user.getNickName(), message);
    }

    public ChatMessageResponse createLeaveMessage(Long partyId, UserResponse user) {
        partyRoomRepository.findById(partyId).orElseThrow();
        return new ChatMessageResponse(MessageType.LEAVE, user.getNickName(),
            user.getNickName() + "님이 퇴장하셨습니다.");
    }
}
