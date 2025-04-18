package com.rendu.backend.controleurs;

import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import com.rendu.backend.service.TaskHistoryService;
import com.rendu.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-history")
@CrossOrigin("*")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;
    private final TaskService taskService;

    @Autowired
    public TaskHistoryController(TaskHistoryService taskHistoryService, TaskService taskService) {
        this.taskHistoryService = taskHistoryService;
        this.taskService = taskService;
    }

    @GetMapping("/task/{taskId}")
    public List<TaskHistory> getHistoryByTask(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return taskHistoryService.getHistoryForTask(task);
    }
}
