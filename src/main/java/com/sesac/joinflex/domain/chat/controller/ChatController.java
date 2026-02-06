package com.sesac.joinflex.domain.chat.controller;

import com.sesac.joinflex.domain.chat.dto.request.ChatMessageRequest;
import com.sesac.joinflex.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflex.domain.chat.service.ChatService;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/party/{partyId}/enter")
    @SendTo("/sub/party/{partyId}")
    public ChatMessageResponse enterUser(@DestinationVariable Long partyId, Principal principal) {
        Authentication authentication = (Authentication) principal;
        UserResponse userResponse = (UserResponse) authentication.getPrincipal();
        return chatService.createEnterMessage(partyId, userResponse);
    }

    @MessageMapping("/party/{partyId}/talk")
    @SendTo("/sub/party/{partyId}")
    public ChatMessageResponse talkUser(@DestinationVariable Long partyId, @Payload
    ChatMessageRequest request, Principal principal) {
        Authentication authentication = (Authentication) principal;
        UserResponse userResponse = (UserResponse) authentication.getPrincipal();
        System.out.println(userResponse.getNickName());
        return chatService.createTalkMessage(partyId, userResponse, request.message());
    }

}
