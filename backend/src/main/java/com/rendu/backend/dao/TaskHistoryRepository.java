package com.rendu.backend.dao;

import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTask(Task task);
}
