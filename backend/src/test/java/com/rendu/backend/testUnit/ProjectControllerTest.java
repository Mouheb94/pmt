package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.ProjectController;
import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.dto.ProjectCreateDto;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Project;
import com.rendu.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProject() {
        // Arrange
        ProjectCreateDto projectDto = new ProjectCreateDto();
        projectDto.setName("Test Project");
        projectDto.setDescription("Test Description");
        projectDto.setStartDate(LocalDate.now());

        ProjectDto createdProject = new ProjectDto();
        createdProject.setId(1L);
        createdProject.setName(projectDto.getName());
        createdProject.setDescription(projectDto.getDescription());
        createdProject.setStartDate(projectDto.getStartDate());

        when(projectService.createProject(any(ProjectCreateDto.class))).thenReturn(createdProject);

        // Act
        ResponseEntity<ProjectDto> response = projectController.createProject(projectDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdProject.getId(), response.getBody().getId());
        assertEquals(createdProject.getName(), response.getBody().getName());
        verify(projectService, times(1)).createProject(any(ProjectCreateDto.class));
    }

    @Test
    void testGetAllProjects() {
        // Arrange
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");

        List<Project> projects = Arrays.asList(project1, project2);
        when(projectService.getAllProjects()).thenReturn(projects);

        // Act
        List<Project> result = projectController.getAllProjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(projects, result);
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetAllProjectsDto() {
        // Arrange
        ProjectDto projectDto1 = new ProjectDto();
        projectDto1.setId(1L);
        projectDto1.setName("Project 1");

        ProjectDto projectDto2 = new ProjectDto();
        projectDto2.setId(2L);
        projectDto2.setName("Project 2");

        List<ProjectDto> projectDtos = Arrays.asList(projectDto1, projectDto2);
        when(projectService.getAllProjectsDto()).thenReturn(projectDtos);

        // Act
        List<ProjectDto> result = projectController.getAllProjectsDto();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(projectDtos, result);
        verify(projectService, times(1)).getAllProjectsDto();
    }

    @Test
    void testGetProjectById() {
        // Arrange
        Long projectId = 1L;
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(projectId);
        projectDto.setName("Test Project");

        when(projectService.getProjectById(projectId)).thenReturn(projectDto);

        // Act
        ProjectDto result = projectController.getProjectById(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals(projectDto.getName(), result.getName());
        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    void testUpdateProject() {
        // Arrange
        Long projectId = 1L;
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(projectId);
        projectDto.setName("Updated Project");
        projectDto.setDescription("Updated Description");

        ProjectDto updatedProject = new ProjectDto();
        updatedProject.setId(projectId);
        updatedProject.setName(projectDto.getName());
        updatedProject.setDescription(projectDto.getDescription());

        when(projectService.updateProject(anyLong(), any(ProjectDto.class))).thenReturn(updatedProject);

        // Act
        ResponseEntity<ProjectDto> response = projectController.updateProject(projectId, projectDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(projectId, response.getBody().getId());
        assertEquals(projectDto.getName(), response.getBody().getName());
        verify(projectService, times(1)).updateProject(projectId, projectDto);
    }

    @Test
    void testInviteMembres() {
        // Arrange
        Long projectId = 1L;
        EmailRole emailRole1 = new EmailRole();
        emailRole1.setEmail("user1@example.com");
        emailRole1.setRole(RoleName.MEMBER);

        EmailRole emailRole2 = new EmailRole();
        emailRole2.setEmail("user2@example.com");
        emailRole2.setRole(RoleName.OBSERVATOR);

        List<EmailRole> emailRoles = Arrays.asList(emailRole1, emailRole2);

        // Act
        projectController.inviteMembres(projectId, emailRoles);

        // Assert
        verify(projectService, times(1)).inviteMembres(projectId, emailRoles);
    }
} 