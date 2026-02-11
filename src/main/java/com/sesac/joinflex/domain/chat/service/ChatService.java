package com.sesac.joinflex.domain.chat.service;

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
        PartyRoom party = getPartyRoom(partyId);

        return ChatMessageResponse.enter(user.getNickName(), party.getCurrentMemberCount());
    }

    public ChatMessageResponse createTalkMessage(Long partyId, UserResponse user, String message) {
        PartyRoom party = getPartyRoom(partyId);

        return ChatMessageResponse.talk(user.getNickName(), message, party.getCurrentMemberCount());
    }

//    public ChatMessageResponse createLeaveMessage(Long partyId, UserResponse user) {
//        partyRoomRepository.findById(partyId).orElseThrow();
//        return new ChatMessageResponse(MessageType.LEAVE, user.getNickName(),
//            user.getNickName() + "님이 퇴장하셨습니다.");
//    }

    private PartyRoom getPartyRoom(Long partyId) {
        return partyRoomRepository.findById(partyId).orElseThrow();
    }
}
