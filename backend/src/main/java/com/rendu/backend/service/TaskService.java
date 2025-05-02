package com.rendu.backend.service;

import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;

import java.util.List;

public interface TaskService {
    Task createTask(Task task,Long id);
    Task updateTask(Long id, Long modifiedBy, Task updatedTask);
    Task getTaskById(Long id);
    List<Task> getAllTasks();


    List<Task> getTasksByStatus(TaskStatus status);

    Task assigneTask(Long userId, Long taskId);

    List<Task> getAllTasksByProjectId(Long projectId);


}
