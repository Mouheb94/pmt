package com.rendu.backend.service.impl;

import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.models.Project;
import com.rendu.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(Long id, Project updatedProject) {
        Optional<Project> optional = projectRepository.findById(id);
        if (optional.isPresent()) {
            Project project = optional.get();
            project.setName(updatedProject.getName());
            project.setDescription(updatedProject.getDescription());
            project.setStartDate(updatedProject.getStartDate());
            return projectRepository.save(project);
        } else {
            throw new RuntimeException("Project not found");
        }
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
