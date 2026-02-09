package com.sesac.joinflex.global.handler;

import com.sesac.joinflex.domain.chat.dto.MessageType;
import com.sesac.joinflex.domain.chat.dto.response.ChatMessageResponse;
import com.sesac.joinflex.domain.party.service.PartyService;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.sesac.joinflex.global.security.JwtProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PARTY_ID_STR = "partyId";
    private static final String SUBSCRIBE_PARTY_PREFIX = "/sub/party/";

    private final JwtProvider jwtProvider;
    private final PartyService partyService;
    private final SimpMessagingTemplate messagingTemplate;

    public StompHandler(JwtProvider jwtProvider, PartyService partyService,
        @Lazy SimpMessagingTemplate messagingTemplate) {
        this.jwtProvider = jwtProvider;
        this.partyService = partyService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisconnect(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = resolveToken(accessor);

        validateToken(token);

        UserResponse userResponse = jwtProvider.getUserResponse(token);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userResponse, null, null);

        accessor.setUser(authenticationToken);
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {

        UserResponse user = getUser(accessor);

        Long partyId = (Long) accessor.getSessionAttributes().get(PARTY_ID_STR);

        if (user != null && partyId != null) {
            processLeave(partyId, user);
        }
    }

    private void processLeave(Long partyId, UserResponse user) {
        partyService.leavePartyRoom(partyId, user.getId())
            .ifPresent(currentCount -> messagingTemplate.convertAndSend(
                SUBSCRIBE_PARTY_PREFIX + partyId,
                new ChatMessageResponse(MessageType.LEAVE, user.getNickName(), user.getNickName() + "님이 퇴장하셨습니다.")
            ));
    }

    private static UserResponse getUser(StompHeaderAccessor accessor) {
        Authentication auth = (Authentication) accessor.getUser();

        if (auth != null && auth.getPrincipal() instanceof UserResponse user) {
            return user;
        }

        return null;
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String rawToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);

        if (rawToken != null && rawToken.startsWith(BEARER_PREFIX)) {
            return rawToken.substring(7);
        }
        return null;
    }

    private void validateToken(String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

}
