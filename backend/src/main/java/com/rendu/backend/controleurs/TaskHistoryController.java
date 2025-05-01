package com.rendu.backend.controleurs;

import com.rendu.backend.dto.TaskHistoryDto;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import com.rendu.backend.service.TaskHistoryService;
import com.rendu.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmt/task-history")
@CrossOrigin("*")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;
    private final TaskService taskService;

    @Autowired
    public TaskHistoryController(TaskHistoryService taskHistoryService, TaskService taskService) {
        this.taskHistoryService = taskHistoryService;
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<TaskHistoryDto>> getHistoryByTask(@PathVariable Long taskId) {
        List<TaskHistoryDto> historyDto = taskHistoryService.getHistoryForTask(taskId);
        return ResponseEntity.ok(historyDto);
    }
}
