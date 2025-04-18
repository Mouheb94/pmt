package com.rendu.backend.controleurs;

import com.rendu.backend.models.Notification;
import com.rendu.backend.models.User;
import com.rendu.backend.service.NotificationService;
import com.rendu.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PostMapping("/send/{userId}")
    public Notification sendNotification(@PathVariable Long userId, @RequestBody String message) {
        User user = userService.getUserById(userId);
        return notificationService.sendNotification(message, user);
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return notificationService.getUserNotifications(user);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
