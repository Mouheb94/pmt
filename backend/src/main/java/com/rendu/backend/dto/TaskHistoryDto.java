package com.rendu.backend.dto;

import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TaskHistoryDto {
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private LocalDateTime modificationDate;
    private String modifiedBy;
    public TaskHistoryDto(TaskHistory taskHistory){
        this.fieldChanged=getFieldChanged();
        this.oldValue=getOldValue();
        this.newValue=getNewValue();
        this.modificationDate=getModificationDate();
        this.modifiedBy=getModifiedBy();

    }
}
