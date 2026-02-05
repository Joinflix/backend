package com.sesac.joinflex.domain.chat.service;

import com.sesac.joinflex.domain.chat.dto.MessageType;
import com.sesac.joinflex.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PartyRoomRepository partyRoomRepository;

    public ChatMessageResponse createEnterMessage(Long partyId, UserResponse user) {
        PartyRoom partyRoom = partyRoomRepository.findById(partyId).orElseThrow();
        return new ChatMessageResponse(MessageType.ENTER, user.getNickName(),
            user.getNickName() + "님이 입장하셨습니다.",
            partyRoom.getCurrentMemberCount());
    }

    public ChatMessageResponse createTalkMessage(Long partyId, UserResponse user, String message) {
        PartyRoom partyRoom = partyRoomRepository.findById(partyId).orElseThrow();
        return new ChatMessageResponse(MessageType.TALK, user.getNickName(),
            message, partyRoom.getCurrentMemberCount());
    }
}
