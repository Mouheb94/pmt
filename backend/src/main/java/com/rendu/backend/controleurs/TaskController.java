package com.rendu.backend.controleurs;


import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Task;
import com.rendu.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmt/tasks")
@CrossOrigin("*")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create/{projectId}")
    public Task createTask(@RequestBody Task task,@PathVariable Long projectId) {
        return taskService.createTask(task,projectId);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/project/{projectId}")
    public List<Task> getAllTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getAllTasksByProjectId(projectId);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }
    @GetMapping("/{status}")
    public List<Task> getTaskByStatus(@PathVariable TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    @PatchMapping ("/assigner/{userId}/{taskId}")
    public Task assigneTask(@PathVariable Long userId,@PathVariable Long taskId) {
        return taskService.assigneTask(userId,taskId);
    }


    @PutMapping("/{idTask}/{idUser}")
    public Task updateTask(@PathVariable Long idTask, @PathVariable Long idUser,@RequestBody Task updatedTask) {
        return taskService.updateTask(idTask, idUser,updatedTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}

