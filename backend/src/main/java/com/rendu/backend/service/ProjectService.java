package com.rendu.backend.service;

import com.rendu.backend.models.Project;

import java.util.List;

public interface ProjectService {
    Project createProject(Project project);
    Project updateProject(Long id, Project updatedProject);
    Project getProjectById(Long id);
    List<Project> getAllProjects();
    void deleteProject(Long id);
}
