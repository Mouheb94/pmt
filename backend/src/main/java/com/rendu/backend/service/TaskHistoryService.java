package com.rendu.backend.service;

import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;

import java.util.List;

public interface TaskHistoryService {
    List<TaskHistory> getHistoryForTask(Task task);
    TaskHistory save(TaskHistory history);
}

