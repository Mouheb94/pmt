package com.rendu.backend.dao;


import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByAssignedToId(Long userId);
}

