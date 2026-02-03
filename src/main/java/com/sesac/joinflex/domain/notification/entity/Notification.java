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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean isRead;

    private Long targetId;
    
    @Builder
    private Notification(User user, NotificationType type, String message, Long targetId){
        this.user = user;
        this.type = type;
        this.message = message;
        this.targetId = targetId;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}