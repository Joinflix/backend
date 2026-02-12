package com.sesac.joinflix.domain.notification.dto.response;

import com.sesac.joinflix.domain.notification.entity.Notification;
import java.time.LocalDateTime;

import com.sesac.joinflix.domain.notification.type.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private NotificationType notificationType;
    private Long senderId;
    private Long receiverId;
    private Long eventId;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .message(notification.getMessage())
            .notificationType(notification.getNotificationType())
            .createdAt(notification.getCreatedAt())
            .senderId(notification.getSenderId())
            .receiverId(notification.getReceiverId())
            .eventId(notification.getEventId())
            .build();
    }
}
