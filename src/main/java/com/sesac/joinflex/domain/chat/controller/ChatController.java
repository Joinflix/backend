package com.sesac.joinflex.domain.chat.controller;

import com.sesac.joinflex.domain.chat.dto.MessageType;
import com.sesac.joinflex.domain.chat.dto.request.ChatMessageRequest;
import com.sesac.joinflex.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflex.domain.chat.service.ChatService;
import com.sesac.joinflex.domain.party.service.PartyService;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import java.security.Principal;
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
        SimpMessageHeaderAccessor headerAccessor) {
        UserResponse user = getUser(principal);

        headerAccessor.getSessionAttributes().remove("partyId");

        return partyService.leavePartyRoom(partyId, user.getId())
            .map(currentCount -> chatService.createLeaveMessage(partyId, user))
            .orElse(null);
    }

    private UserResponse getUser(Principal principal) {
        Authentication authentication = (Authentication) principal;
        return (UserResponse) authentication.getPrincipal();
    }

}
