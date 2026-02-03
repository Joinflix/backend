package com.sesac.joinflex.domain.notification.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = TimeUnit.HOURS.toMillis(1); // 1시간

    // SSE 구독
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterMap.put(userId, emitter);

        // 연결 종료 시 삭제
        emitter.onCompletion(() -> emitterMap.remove(userId));

        // 타임아웃 시 종료
        emitter.onTimeout(() -> emitterMap.remove(userId));

        // 503 방지
        send(userId, "EventStream Created. [userId=" + userId + "]");

        return emitter;
    }

    // 알림 전송
    public void send(Long userId, Object data) {
        SseEmitter emitter = emitterMap.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(data));
            } catch (IOException e) {
                emitterMap.remove(userId);
            }
        }
    }
}
