package com.rendu.backend.service.impl;

import com.rendu.backend.dao.ProjectMemberRepository;
import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.RoleRepository;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.dto.ProjectCreateDto;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.exception.ResourceNotFoundException;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.Role;
import com.rendu.backend.models.User;
import com.rendu.backend.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectMemberRepository projectMemberRepository;



    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, RoleRepository roleRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.projectMemberRepository = projectMemberRepository;
    }
    @Override
    public ProjectDto createProject(ProjectCreateDto projectDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User creator = userRepository.findByEmail(email);

        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate().atStartOfDay());
        project.setCreatedBy(creator);
        project.setMembers(new HashSet<>());
        Project savedProject = projectRepository.save(project);
        ProjectMember projectMember = ProjectMember.builder()
                .user(creator)
                .project(savedProject)
                .role(RoleName.ADMIN)
                .build();
        projectMemberRepository.save(projectMember);
        savedProject.getMembers().add(projectMember);
        return new ProjectDto(savedProject);
    }



    @Override
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (projectDto.getName() != null) {
            existingProject.setName(projectDto.getName());
        }
        if (projectDto.getDescription() != null) {
            existingProject.setDescription(projectDto.getDescription());
        }
        if (projectDto.getStartDate() != null) {
            existingProject.setStartDate(projectDto.getStartDate().atStartOfDay());
        }
        if (projectDto.getCreatedById() != null) {
            User creator = userRepository.findById(projectDto.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + projectDto.getCreatedById()));
            existingProject.setCreatedBy(creator);
        }

        Project updatedProject = projectRepository.save(existingProject);
        return new ProjectDto(updatedProject);
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
                   return new ProjectDto(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return  projectRepository.findAll();
    }

    @Override
    public List<ProjectDto> getAllProjectsDto() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }

    @Override
        public void deleteProject(Long id) {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
            projectRepository.delete(project);
        }

    @Override
    public void inviteMembres(Long projectId, List<EmailRole> emailRoles) {
        // 1. Charger le projet et initialiser la collection
        Optional<Project> project = projectRepository.findById(projectId);

        // 2. Créer une nouvelle collection basée sur l'existante
        Set<ProjectMember> updatedMembers = new HashSet<>(project.get().getMembers());

        // 3. Traiter les invitations
        for (EmailRole emailRole : emailRoles) {
            User user = userRepository.findByEmail(emailRole.getEmail());
            if (user == null) continue;



            boolean isAlreadyMember = updatedMembers.stream()
                    .anyMatch(m -> m.getUser().getId().equals(user.getId()));

            if (!isAlreadyMember) {
                ProjectMember member = new ProjectMember(user, project.get(), emailRole.getRole());
                updatedMembers.add(member);
                projectMemberRepository.save(member);
            }
        }

        // 4. Mettre à jour la collection sans la remplacer
        project.get().getMembers().clear();
        project.get().getMembers().addAll(updatedMembers);
        projectRepository.save(project.get());
    }

}
