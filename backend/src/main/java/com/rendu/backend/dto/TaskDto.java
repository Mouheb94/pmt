package com.rendu.backend.dto;

import com.rendu.backend.enums.Priority;
import com.rendu.backend.models.Task;

import java.time.LocalDate;


import com.rendu.backend.enums.Priority;
import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private TaskStatus status;
    private Long assignedToId;
    private String assignedToUsername;
    private Long createdById;
    private String createdByUsername;

    public TaskDto(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate();
        this.priority = task.getPriority();
        this.status = task.getStatus();
        if (task.getAssignedTo() != null) {
            this.assignedToId = task.getAssignedTo().getId();
            this.assignedToUsername = task.getAssignedTo().getUsername();
        }
        this.createdById = task.getCreatedBy().getId();
        this.createdByUsername = task.getCreatedBy().getUsername();
    }
}
