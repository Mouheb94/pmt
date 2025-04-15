package com.rendu.backend.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La tâche concernée
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // L'utilisateur ayant effectué le changement (optionnel mais recommandé)
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

    public TaskHistory() {}

    public TaskHistory(Task task, User modifiedBy, String fieldChanged, String oldValue, String newValue) {
        this.task = task;
        this.modifiedBy = modifiedBy;
        this.modificationDate = LocalDateTime.now();
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
