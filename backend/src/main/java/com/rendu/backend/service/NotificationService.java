package com.rendu.backend.service;

import com.rendu.backend.models.Notification;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(String message, User recipient);
    List<Notification> getUserNotifications(User user);
    void markAsRead(Long id);
}
