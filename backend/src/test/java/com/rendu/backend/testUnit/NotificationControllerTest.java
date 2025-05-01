package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.NotificationController;
import com.rendu.backend.models.Notification;
import com.rendu.backend.models.User;
import com.rendu.backend.service.NotificationService;
import com.rendu.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification() {
        // Arrange
        Long userId = 1L;
        String message = "Test notification message";
        
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage(message);
        notification.setUser(user);
        notification.setSentDate(LocalDate.now());
        
        when(userService.getUserById(userId)).thenReturn(user);
        when(notificationService.sendNotification(any(String.class), any(User.class))).thenReturn(notification);

        // Act
        Notification result = notificationController.sendNotification(userId, message);

        // Assert
        assertNotNull(result);
        assertEquals(notification.getId(), result.getId());
        assertEquals(message, result.getMessage());
        assertEquals(userId, result.getUser().getId());
        verify(userService, times(1)).getUserById(userId);
        verify(notificationService, times(1)).sendNotification(message, user);
    }

    @Test
    void testGetUserNotifications() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setMessage("Notification 1");
        notification1.setUser(user);
        
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setMessage("Notification 2");
        notification2.setUser(user);
        
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        
        when(userService.getUserById(userId)).thenReturn(user);
        when(notificationService.getUserNotifications(user)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationController.getUserNotifications(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(notifications, result);
        verify(userService, times(1)).getUserById(userId);
        verify(notificationService, times(1)).getUserNotifications(user);
    }

    @Test
    void testMarkAsRead() {
        // Arrange
        Long notificationId = 1L;

        // Act
        notificationController.markAsRead(notificationId);

        // Assert
        verify(notificationService, times(1)).markAsRead(notificationId);
    }
} 