package com.rendu.backend.service;

import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.dto.ProjectCreateDto;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.models.Project;

import java.util.List;

public interface ProjectService {

    ProjectDto updateProject(Long id, ProjectDto projectDto);

    ProjectDto getProjectById(Long id);
    List<Project> getAllProjects();
    List<ProjectDto> getAllProjectsDto();
    void inviteMembres(Long projectId, List<EmailRole> emailRoles);
    ProjectDto createProject(ProjectCreateDto projectDto);
}
