package com.rendu.backend.service.impl;


import com.rendu.backend.dao.TaskHistoryRepository;
import com.rendu.backend.dto.TaskHistoryDto;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import com.rendu.backend.models.User;
import com.rendu.backend.service.TaskHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    @Autowired
    public TaskHistoryServiceImpl(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    public void logTaskChange(Task task, String changedField, String oldValue, String newValue, User modifiedBy) {
        TaskHistory history = TaskHistory.builder()
                .task(task)
                .fieldChanged(changedField)
                .oldValue(oldValue)
                .newValue(newValue)
                .modifiedBy(modifiedBy)
                .modificationDate(LocalDateTime.now())
                .build();

        taskHistoryRepository.save(history);
    }

    @Override
    public List<TaskHistoryDto> getHistoryForTask(Long taskId) {
        return taskHistoryRepository.findByTaskId(taskId).stream()
                .map(history -> new TaskHistoryDto(
                        history.getFieldChanged(),
                        history.getOldValue(),
                        history.getNewValue(),
                        history.getModificationDate(),
                        history.getModifiedBy().getUsername()
                ))
                .collect(Collectors.toList());
    }

}
