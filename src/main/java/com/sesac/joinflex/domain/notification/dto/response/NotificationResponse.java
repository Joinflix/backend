package com.sesac.joinflex.domain.notification.dto.response;

import com.sesac.joinflex.domain.notification.entity.Notification;
import com.sesac.joinflex.domain.notification.type.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private Boolean isRead;
    private Long targetId;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .type(notification.getType())
            .message(notification.getMessage())
            .isRead(notification.getIsRead())
            .targetId(notification.getTargetId())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
