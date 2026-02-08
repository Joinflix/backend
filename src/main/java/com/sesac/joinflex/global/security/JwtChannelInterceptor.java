package com.sesac.joinflex.global.security;

import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
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
