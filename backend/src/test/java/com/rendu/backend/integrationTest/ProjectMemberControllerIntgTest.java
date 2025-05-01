package com.rendu.backend.integrationTest;

import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.ProjectMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProjectMemberControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/pmt/project-members";

    @BeforeEach
    @Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void setup() {
        // Les données de test sont chargées via le script SQL
    }

    @Test
    public void testAddMember() {
        // Arrange
        ProjectMember member = new ProjectMember();
        member.setRole(RoleName.MEMBER);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProjectMember> request = new HttpEntity<>(member, headers);

        // Act
        ResponseEntity<ProjectMember> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                ProjectMember.class
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RoleName.MEMBER, response.getBody().getRole());
        assertNotNull(response.getBody().getUser());
        assertNotNull(response.getBody().getProject());
    }

    @Test
    public void testGetMembersByProject() {
        // Arrange
        Long projectId = 1L;

        // Act
        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/{projectId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Vérification des membres du projet
        List<ProjectMember> members = response.getBody();
        members.forEach(member -> {
            assertNotNull(member.getUser());
            assertNotNull(member.getRole());
            assertEquals(projectId, member.getProject().getId());
        });
    }

    @Test
    public void testGetProjectsByUser() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/user/{userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                userId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Vérification des projets de l'utilisateur
        List<ProjectMember> projects = response.getBody();
        projects.forEach(projectMember -> {
            assertNotNull(projectMember.getProject());
            assertNotNull(projectMember.getRole());
            assertEquals(userId, projectMember.getUser().getId());
        });
    }

    @Test
    public void testGetMembersByProject_NoMembers() {
        // Arrange
        Long projectId = 999L; // ID d'un projet inexistant

        // Act
        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/{projectId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testGetProjectsByUser_NoProjects() {
        // Arrange
        Long userId = 999L; // ID d'un utilisateur inexistant

        // Act
        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/user/{userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                userId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
} 