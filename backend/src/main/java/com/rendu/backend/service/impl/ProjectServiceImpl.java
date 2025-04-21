package com.rendu.backend.service.impl;

import com.rendu.backend.dao.ProjectMemberRepository;
import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.RoleRepository;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.Role;
import com.rendu.backend.models.User;
import com.rendu.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public void inviteMembres(Long projectId, List<EmailRole> emailRoles) {
        Optional<Project> project = projectRepository.findById(projectId);
        Set<ProjectMember> members=new HashSet<>();
        for (EmailRole emailRole : emailRoles) {
            User user = userRepository.findByEmail(emailRole.getEmail());
            if (user != null) {
                Optional<Role> role = roleRepository.findByName(emailRole.getRole());
                if (role.isPresent()) {
                    user.getRoles().add(role.get());
                    userRepository.save(user); // sauvegarde les modifications

                    if (project.isPresent()) {
                        ProjectMember projectMember = new ProjectMember(user, project.get(), role.get().getName());
                        projectMemberRepository.save(projectMember);
                        members.add(projectMember);


                    }
                }
            }
        }
        if (project.isPresent()) {
        project.get().setMembers(members);
        projectRepository.save(project.get());
        }
    }

}
