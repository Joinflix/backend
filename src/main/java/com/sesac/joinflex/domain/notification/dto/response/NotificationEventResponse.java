package com.sesac.joinflex.domain.notification.dto.response;

import com.sesac.joinflex.domain.notification.type.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEventResponse {
    private NotificationType type;
    private String message;
    private Long targetId;
}
