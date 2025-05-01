package com.rendu.backend.service;

import com.rendu.backend.dto.TaskHistoryDto;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;

import java.util.List;

public interface TaskHistoryService {
    List<TaskHistoryDto> getHistoryForTask(Long taskId);
    void logTaskChange(Task task, String changedField, String oldValue, String newValue, User modifiedBy);
}

