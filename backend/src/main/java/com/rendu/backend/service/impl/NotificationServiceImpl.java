package com.rendu.backend.service.impl;

import com.rendu.backend.dao.NotificationRepository;
import com.rendu.backend.models.Notification;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;
import com.rendu.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification sendNotification(String message, User recipient) {
        Notification notification = new Notification(message, recipient);
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUser(user);
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadNot(true);
        notificationRepository.save(notification);
    }
}

