package com.rendu.backend.dao;

import com.rendu.backend.models.Notification;
import com.rendu.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserAndReadNotFalse(User user);
}
