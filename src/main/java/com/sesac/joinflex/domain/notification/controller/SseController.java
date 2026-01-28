package com.sesac.joinflex.domain.notification.controller;

import com.sesac.joinflex.domain.friend.service.FriendRequestService;
import com.sesac.joinflex.domain.notification.service.SseNotificationListener;
import com.sesac.joinflex.domain.notification.service.SseService;
import com.sesac.joinflex.global.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30분

    private final SseService sseService;
    private final SseNotificationListener notificationListener;
    private final CurrentUserResolver currentUserResolver;
    private final FriendRequestService friendRequestService;

    // GET /api/sse/subscribe
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpServletRequest request) {
        Long userId = currentUserResolver.resolve(request);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        sseService.register(userId, emitter);


        Runnable onDisconnect = createDisconnectHandler(userId, emitter);
        emitter.onCompletion(onDisconnect);
        emitter.onTimeout(onDisconnect);
        emitter.onError(e -> {
            log.warn("SSE error for userId: {}", userId, e);
            onDisconnect.run();
        });

        // 연결 성공 이벤트 전송
        if (!sendOpenEvent(emitter, userId)) {
            return emitter;
        }

        // 온라인 상태 알림
        notificationListener.markOnline(userId);

        log.info("SSE subscribed: userId={}", userId);
        return emitter;
    }

    private Runnable createDisconnectHandler(Long userId, SseEmitter emitter) {
        return () -> {
            sseService.remove(userId, emitter);
            if (!sseService.isOnline(userId)) {
                notificationListener.markOffline(userId);
            }
        };
    }

    private boolean sendOpenEvent(SseEmitter emitter, Long userId) {
        try {
            List<Long> friendIds = friendRequestService.getFriendIds(userId);
            List<Long> onlineFriends = sseService.getOnlineUserIds(friendIds);

            Map<String, Object> openData = Map.of(
                "message", "connected",
                "onlineFriends", onlineFriends
            );

            emitter.send(SseEmitter.event()
                .name("open")
                .data(openData)
                .reconnectTime(3000));
            return true;
        } catch (IOException e) {
            log.error("Failed to send open event for userId: {}", userId, e);
            sseService.remove(userId, emitter);
            return false;
        }
    }
}
