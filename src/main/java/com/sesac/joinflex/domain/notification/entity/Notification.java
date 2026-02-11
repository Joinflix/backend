package com.sesac.joinflex.domain.notification.entity;

import com.sesac.joinflex.domain.notification.type.NotificationType;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    private Long senderId;

    private Long receiverId;

    private String eventId;

    private LocalDateTime lastReadAt;

    private Notification(User user, String message, NotificationType notificationType,
                         Long senderId, Long receiverId, String eventId) {
        this.user = user;
        this.message = message;
        this.notificationType = notificationType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.eventId = eventId;
        this.lastReadAt = null;
    }

    public static Notification create(User user, String message, NotificationType notificationType,
                                      Long senderId, Long receiverId, String eventId) {
        return new Notification(user, message, notificationType, senderId, receiverId, eventId);
    }

    public void updateLastReadAt(LocalDateTime now) {
        this.lastReadAt = now;
    }
}