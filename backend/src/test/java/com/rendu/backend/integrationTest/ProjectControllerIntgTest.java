package com.rendu.backend.integrationTest;

import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.dto.ProjectCreateDto;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProjectControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/pmt/projects";

    @BeforeEach
    @Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void setup() {
        // Les données de test sont chargées via le script SQL
    }

    @Test
    public void testCreateProject() {
        // Arrange
        ProjectCreateDto projectDto = new ProjectCreateDto();
        projectDto.setName("Test Project");
        projectDto.setDescription("Test Description");
        projectDto.setStartDate(LocalDate.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProjectCreateDto> request = new HttpEntity<>(projectDto, headers);

        // Act
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/create",
                HttpMethod.POST,
                request,
                ProjectDto.class
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(projectDto.getName(), response.getBody().getName());
        assertEquals(projectDto.getDescription(), response.getBody().getDescription());
        assertNotNull(response.getBody().getStartDate());
    }

    @Test
    public void testGetAllProjects() {
        // Act
        ResponseEntity<List<Project>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Project>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetAllProjectsDto() {
        // Act
        ResponseEntity<List<ProjectDto>> response = restTemplate.exchange(
                BASE_URL + "/details",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectDto>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetProjectById() {
        // Arrange
        Long projectId = 1L;

        // Act
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.GET,
                null,
                ProjectDto.class,
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(projectId, response.getBody().getId());
    }

    @Test
    public void testUpdateProject() {
        // Arrange
        Long projectId = 1L;
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Updated Project Name");
        projectDto.setDescription("Updated Description");
        projectDto.setStartDate(LocalDate.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProjectDto> request = new HttpEntity<>(projectDto, headers);

        // Act
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                ProjectDto.class,
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(projectDto.getName(), response.getBody().getName());
        assertEquals(projectDto.getDescription(), response.getBody().getDescription());
    }

    @Test
    public void testInviteMembres() {
        // Arrange
        Long projectId = 1L;
        EmailRole emailRole = new EmailRole();
        emailRole.setEmail("test@example.com");
        emailRole.setRole(RoleName.valueOf("MEMBER"));
        List<EmailRole> emailRoles = List.of(emailRole);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<EmailRole>> request = new HttpEntity<>(emailRoles, headers);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/invite/{projectId}",
                HttpMethod.POST,
                request,
                Void.class,
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetProjectById_NotFound() {
        // Arrange
        Long projectId = 999L; // ID inexistant

        // Act
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.GET,
                null,
                ProjectDto.class,
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateProject_NotFound() {
        // Arrange
        Long projectId = 999L; // ID inexistant
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Updated Project Name");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProjectDto> request = new HttpEntity<>(projectDto, headers);

        // Act
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                ProjectDto.class,
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 