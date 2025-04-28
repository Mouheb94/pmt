package com.rendu.backend.service;

import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Notification;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;

import java.util.List;

public interface TaskService {
    Task createTask(Task task,Long id);
    Task updateTask(Long id, Task updatedTask);
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    void deleteTask(Long id);

    List<Task> getTasksByStatus(TaskStatus status);

    Task assigneTask(Long userId, Long taskId);

    List<Task> getAllTasksByProjectId(Long projectId);

    interface NotificationService {
        Notification sendNotification(String message, User recipient);
        List<Notification> getUserNotifications(User user);
        void markAsRead(Long id);
    }
}
