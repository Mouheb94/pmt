package com.rendu.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rendu.backend.enums.Priority;
import com.rendu.backend.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    // The user assigned to the task

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;


    public Task(String name, String description, LocalDate dueDate, Priority priority, Project project,User assignedTo,User createdBy) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.project = project;
        this.assignedTo= assignedTo;
        this.createdBy= createdBy;
    }


    public Task(Long id, String name, String description, LocalDate dueDate, Priority priority, TaskStatus status, Long id1, Long aLong, Long id2) {
    }
}

