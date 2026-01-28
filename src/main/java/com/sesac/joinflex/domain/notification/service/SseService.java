package com.sesac.joinflex.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final ObjectMapper objectMapper;

    // userId -> Set<SseEmitter> (멀티탭/멀티디바이스 지원)
    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();



    public void register(Long userId, SseEmitter emitter) {
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>())
            .add(emitter);
    }


    public void remove(Long userId, SseEmitter emitter) {
        Set<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }


    public boolean isOnline(Long userId) {
        Set<SseEmitter> userEmitters = emitters.get(userId);
        return userEmitters != null && !userEmitters.isEmpty();
    }

    public List<Long> getOnlineUserIds(List<Long> userIds) {
        return userIds.stream()
            .filter(this::isOnline)
            .toList();
    }

    public void send(Long userId, String eventType, Object data) {
        Set<SseEmitter> userEmitters = emitters.getOrDefault(userId, Set.of());
        if (userEmitters.isEmpty()) {
            return;
        }

        String jsonData = buildPayload(data);
        if (jsonData == null) {
            return;
        }

        SseEmitter.SseEventBuilder event = SseEmitter.event()
            .id(UUID.randomUUID().toString())
            .name(eventType)
            .data(jsonData);

        sendToEmitters(userId, userEmitters, event);
    }

    public void sendToMany(List<Long> userIds, String eventType, Object data) {
        for (Long userId : userIds) {
            send(userId, eventType, data);
        }
    }


    // 25초 주기로 모든 SSE 연결에 heartbeat 전송
    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        for (Map.Entry<Long, Set<SseEmitter>> entry : emitters.entrySet()) {
            Long userId = entry.getKey();
            for (SseEmitter emitter : entry.getValue()) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    remove(userId, emitter);
                }
            }
        }
    }


    private String buildPayload(Object data) {
        try {
            Map<String, Object> payload = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "at", LocalDateTime.now().toString(),
                "data", data
            );
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SSE payload", e);
            return null;
        }
    }

    private void sendToEmitters(Long userId, Set<SseEmitter> userEmitters, SseEmitter.SseEventBuilder event) {
        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                log.warn("Failed to send SSE event to userId: {}, removing emitter", userId);
                remove(userId, emitter);
            }
        }
    }
}
