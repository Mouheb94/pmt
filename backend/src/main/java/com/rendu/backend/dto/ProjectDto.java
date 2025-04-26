package com.rendu.backend.dto;

import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private Long createdById;
    private List<ProjectMemberDto> members;

    private List<Task> tasks;
    public ProjectDto(Project project){
        this.id=project.getId();
        this.name=project.getName();
        this.description=project.getDescription();
        this.startDate=project.getStartDate().toLocalDate();
        this.createdById=project.getCreatedBy().getId();
        this.members = project.getMembers()
                .stream()
                .map(member -> new ProjectMemberDto(
                        member.getUser().getId(),
                        member.getUser().getUsername(),
                        member.getRole()))
                .toList();
        this.tasks = project.getTasks()
                .stream()
                .map(task -> new Task(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getDueDate(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getCreatedBy().getId(),
                        task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                        task.getProject().getId()))
                .toList();



    }

}
