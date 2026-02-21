package com.sesac.joinflix.domain.chat.service;

import com.sesac.joinflix.domain.chat.dto.request.VideoSyncRequest;
import com.sesac.joinflix.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflix.domain.chat.dto.response.VideoSyncResponse;
import com.sesac.joinflix.domain.party.entity.PartyRoom;
import com.sesac.joinflix.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflix.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PartyRoomRepository partyRoomRepository;

    public ChatMessageResponse createEnterMessage(Long partyId, UserResponse user) {
        PartyRoom party = getPartyRoom(partyId);

        return ChatMessageResponse.enter(user.getNickName(), party.getCurrentMemberCount(), user.getId());
    }

    public ChatMessageResponse createTalkMessage(Long partyId, UserResponse user, String message) {
        PartyRoom party = getPartyRoom(partyId);

        return ChatMessageResponse.talk(user.getNickName(), message, party.getCurrentMemberCount(), user.getId());
    }

    public static ChatMessageResponse createLeaveMessage(UserResponse user,
        Integer currentMemberCount) {
        return ChatMessageResponse.leave(user.getNickName(), currentMemberCount, user.getId());
    }

    public VideoSyncResponse createSyncMessage(UserResponse user, VideoSyncRequest request) {
        return VideoSyncResponse.of(user.getNickName(), request.currentTime(), request.paused(),
            request.action(), user.getId());
    }

    private PartyRoom getPartyRoom(Long partyId) {
        return partyRoomRepository.findById(partyId).orElseThrow();
    }
}
