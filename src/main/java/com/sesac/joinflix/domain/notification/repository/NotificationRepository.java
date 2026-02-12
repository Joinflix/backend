package com.sesac.joinflix.domain.notification.repository;

import com.sesac.joinflix.domain.notification.entity.Notification;
import com.sesac.joinflix.domain.user.entity.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndReadAtIsNull(User user);
}
