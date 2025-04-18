package com.rendu.backend.service.impl;


import com.rendu.backend.dao.TaskHistoryRepository;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import com.rendu.backend.service.TaskHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    @Autowired
    public TaskHistoryServiceImpl(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    @Override
    public List<TaskHistory> getHistoryForTask(Task task) {
        return taskHistoryRepository.findByTask(task);
    }

    @Override
    public TaskHistory save(TaskHistory history) {
        return taskHistoryRepository.save(history);
    }
}
