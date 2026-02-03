package com.sesac.joinflex.domain.notification.dto.response;

import com.sesac.joinflex.domain.notification.entity.Notification;
import java.time.LocalDateTime;

import com.sesac.joinflex.domain.notification.type.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private NotificationType notificationType;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .message(notification.getMessage()).notificationType(notification.getNotificationType()).createdAt(notification.getCreatedAt()).build();
    }
}
