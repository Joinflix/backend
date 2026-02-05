package com.sesac.joinflex.domain.notification.repository;

import com.sesac.joinflex.domain.notification.entity.Notification;
import com.sesac.joinflex.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);
    List<Notification> findByUserAndCreatedAtAfter(User user, LocalDateTime createdAt);
}
