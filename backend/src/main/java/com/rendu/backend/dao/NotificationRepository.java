package com.rendu.backend.dao;

import com.rendu.backend.models.Notification;
import com.rendu.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User user);
    List<Notification> findByRecipientAndReadFalse(User user);
}
