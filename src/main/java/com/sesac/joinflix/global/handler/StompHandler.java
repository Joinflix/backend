package com.sesac.joinflix.global.handler;

import com.sesac.joinflix.domain.chat.service.ChatService;
import com.sesac.joinflix.domain.party.service.PartyService;
import com.sesac.joinflix.domain.user.dto.response.UserResponse;
import com.sesac.joinflix.global.exception.CustomException;
import com.sesac.joinflix.global.exception.ErrorCode;
import com.sesac.joinflix.global.security.JwtProvider;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
    private final ScheduledExecutorService scheduledExecutorService;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> pendingLeaveMap;

    public StompHandler(JwtProvider jwtProvider, PartyService partyService,
        @Lazy SimpMessagingTemplate messagingTemplate) {
        this.jwtProvider = jwtProvider;
        this.partyService = partyService;
        this.messagingTemplate = messagingTemplate;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.pendingLeaveMap = new ConcurrentHashMap<>();
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisconnect(accessor);
        }

        return message;
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (!destination.startsWith(SUBSCRIBE_PARTY_PREFIX)) {
            return;
        }

        String[] parts = destination.split("/");
        String partyId = parts[parts.length - 1];

        UserResponse user = getUser(accessor);
        String key = String.format("%s:%d", partyId, user.getId());

        ScheduledFuture<?> future = pendingLeaveMap.remove(key);
        if (future != null) {
            future.cancel(false);
        }
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
            String key = String.format("%s:%s", partyId, user.getId());
            pendingLeaveMap.put(key,
                scheduledExecutorService.schedule(() -> {
                    processLeave(partyId, user);
                    pendingLeaveMap.remove(key);
                }, 10, TimeUnit.SECONDS));

        }

        accessor.getSessionAttributes().remove("partyId");
    }

    private void processLeave(Long partyId, UserResponse user) {
        Optional<Integer> result = partyService.leavePartyRoom(partyId, user.getId(),
            null);

        messagingTemplate.convertAndSend(SUBSCRIBE_PARTY_PREFIX + partyId,
            Objects.requireNonNull(
                result.map(leaveResult -> ChatService.createLeaveMessage(user, leaveResult))
                    .orElse(null)));
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
