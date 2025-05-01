package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.ProjectMemberController;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.User;
import com.rendu.backend.service.ProjectMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectMemberControllerTest {

    @Mock
    private ProjectMemberService projectMemberService;

    @InjectMocks
    private ProjectMemberController projectMemberController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMember() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProject(project);
        member.setRole(RoleName.MEMBER);

        ProjectMember savedMember = new ProjectMember();
        savedMember.setId(1L);
        savedMember.setUser(user);
        savedMember.setProject(project);
        savedMember.setRole(RoleName.MEMBER);

        when(projectMemberService.addMemberToProject(any(ProjectMember.class))).thenReturn(savedMember);

        // Act
        ProjectMember result = projectMemberController.addMember(member);

        // Assert
        assertNotNull(result);
        assertEquals(savedMember.getId(), result.getId());
        assertEquals(savedMember.getUser().getId(), result.getUser().getId());
        assertEquals(savedMember.getProject().getId(), result.getProject().getId());
        assertEquals(savedMember.getRole(), result.getRole());
        verify(projectMemberService, times(1)).addMemberToProject(any(ProjectMember.class));
    }

    @Test
    void testGetMembersByProject() {
        // Arrange
        Long projectId = 1L;
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");

        ProjectMember member1 = new ProjectMember();
        member1.setId(1L);
        member1.setUser(user1);
        member1.setProject(project);
        member1.setRole(RoleName.ADMIN);

        ProjectMember member2 = new ProjectMember();
        member2.setId(2L);
        member2.setUser(user2);
        member2.setProject(project);
        member2.setRole(RoleName.MEMBER);

        List<ProjectMember> members = Arrays.asList(member1, member2);
        when(projectMemberService.getMembersByProject(projectId)).thenReturn(members);

        // Act
        List<ProjectMember> result = projectMemberController.getMembersByProject(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(members, result);
        verify(projectMemberService, times(1)).getMembersByProject(projectId);
    }

    @Test
    void testGetProjectsByUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");

        ProjectMember member1 = new ProjectMember();
        member1.setId(1L);
        member1.setUser(user);
        member1.setProject(project1);
        member1.setRole(RoleName.ADMIN);

        ProjectMember member2 = new ProjectMember();
        member2.setId(2L);
        member2.setUser(user);
        member2.setProject(project2);
        member2.setRole(RoleName.MEMBER);

        List<ProjectMember> members = Arrays.asList(member1, member2);
        when(projectMemberService.getProjectsByUser(userId)).thenReturn(members);

        // Act
        List<ProjectMember> result = projectMemberController.getProjectsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(members, result);
        verify(projectMemberService, times(1)).getProjectsByUser(userId);
    }
} 