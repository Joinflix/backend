package com.sesac.joinflex.domain.notification.service;

import com.sesac.joinflex.domain.notification.dto.response.NotificationResponse;
import com.sesac.joinflex.domain.notification.entity.Notification;
import com.sesac.joinflex.domain.notification.repository.NotificationRepository;
import com.sesac.joinflex.domain.notification.type.NotificationType;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = TimeUnit.HOURS.toMillis(1); // 1시간

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    // SSE 구독
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        User user = getUser(userId);

        emitterMap.put(user.getId(), emitter);

        // 연결 종료 시 삭제
        emitter.onCompletion(() -> emitterMap.remove(user.getId()));

        // 타임아웃 시 종료
        emitter.onTimeout(() -> emitterMap.remove(user.getId()));

        List<Notification> notifications = notificationRepository.findByUserAndLastReadAtIsNull(user);

        List<NotificationResponse> unreadNotifications = notifications.stream()
            .map(NotificationResponse::from)
            .toList();

        if (!unreadNotifications.isEmpty()) {
            send(user.getId(), unreadNotifications);
            return emitter;
        }

        // 503 방지
        send(userId, "EventStream Created. [userId=" + userId + "]");

        return emitter;
    }

    // 알림 전송
    private void send(Long userId, Object data) {
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

    public void sendAndSave(Long userId, String message, NotificationType notificationType,
                            Long senderId, Long receiverId, String eventId) {
        Notification saved = notificationRepository.save(
            Notification.create(getUser(userId), message, notificationType, senderId, receiverId, eventId));
        send(userId, NotificationResponse.from(saved));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateLastReadAt(Long userId, Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_YOURS);
        }

        notification.updateLastReadAt(LocalDateTime.now());
    }
}
