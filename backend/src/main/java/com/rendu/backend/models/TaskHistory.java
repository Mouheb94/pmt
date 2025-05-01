package com.rendu.backend.models;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La tâche concernée
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // L'utilisateur ayant effectué le changement
    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

    @Column(name = "modification_date", nullable = false)
    private LocalDateTime modificationDate;

    @Column(name = "field_changed")
    private String fieldChanged;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    public TaskHistory(){}
    public TaskHistory(Long id, Task task, User modifiedBy, LocalDateTime modificationDate,
                       String fieldChanged, String oldValue, String newValue) {
        this.id = id;
        this.task = task;
        this.modifiedBy = modifiedBy;
        this.modificationDate = modificationDate;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
