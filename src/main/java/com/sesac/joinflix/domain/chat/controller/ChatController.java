package com.sesac.joinflix.domain.chat.controller;

import com.sesac.joinflix.domain.chat.dto.request.ChatMessageRequest;
import com.sesac.joinflix.domain.chat.dto.request.LeaveRequest;
import com.sesac.joinflix.domain.chat.dto.request.VideoSyncRequest;
import com.sesac.joinflix.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflix.domain.chat.dto.response.VideoSyncResponse;
import com.sesac.joinflix.domain.chat.service.ChatService;
import com.sesac.joinflix.domain.party.service.PartyService;
import com.sesac.joinflix.domain.user.dto.response.UserResponse;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final PartyService partyService;

    @MessageMapping("/party/{partyId}/enter")
    @SendTo("/sub/party/{partyId}")
    public ChatMessageResponse enterUser(@DestinationVariable Long partyId, Principal principal,
        SimpMessageHeaderAccessor headerAccessor) {
        UserResponse userResponse = getUser(principal);

        headerAccessor.getSessionAttributes().put("partyId", partyId);

        return chatService.createEnterMessage(partyId, userResponse);
    }

    @MessageMapping("/party/{partyId}/talk")
    @SendTo("/sub/party/{partyId}")
    public ChatMessageResponse talkUser(@DestinationVariable Long partyId, @Payload
    ChatMessageRequest request, Principal principal) {
        UserResponse userResponse = getUser(principal);

        return chatService.createTalkMessage(partyId, userResponse, request.message());
    }

    @MessageMapping("/party/{partyId}/leave")
    @SendTo("/sub/party/{partyId}")
    public ChatMessageResponse leaveUser(@DestinationVariable Long partyId, Principal principal,
        @Payload(required = false) LeaveRequest request, SimpMessageHeaderAccessor headerAccessor) {
        UserResponse user = getUser(principal);

        Optional<Integer> result = partyService.leavePartyRoom(partyId, user.getId(),
            request);

        result.ifPresent(r -> headerAccessor.getSessionAttributes().remove("partyId"));

        return result.map(leaveResult -> chatService.createLeaveMessage(user, leaveResult))
            .orElse(null);
    }

    @MessageMapping("/party/{partyId}/video")
    @SendTo("/sub/party/{partyId}/video")
    public VideoSyncResponse syncVideo(@DestinationVariable Long partyId, Principal principal,
        @Payload VideoSyncRequest request) {
        UserResponse user = getUser(principal);

        if (!partyService.canControlVideo(partyId, user.getId())) {
            return null;
        }
        return chatService.createSyncMessage(user, request);
    }

    private UserResponse getUser(Principal principal) {
        Authentication authentication = (Authentication) principal;
        return (UserResponse) authentication.getPrincipal();
    }

}
