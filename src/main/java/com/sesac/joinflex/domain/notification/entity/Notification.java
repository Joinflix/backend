package com.sesac.joinflex.domain.notification.entity;

import com.sesac.joinflex.domain.notification.type.NotificationType;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private NotificationType notificationType;

    private Notification(User user, String message, NotificationType notificationType) {
        this.user = user;
        this.message = message;
        this.notificationType = notificationType;
    }

    public static Notification create(User user, String message, NotificationType notificationType) {
        return new Notification(user, message, notificationType);
    }
}