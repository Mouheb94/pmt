package com.rendu.backend.integrationTest;

import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.dto.AuthDto;
import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.dto.ProjectCreateDto;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProjectControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private static final String BASE_URL = "/pmt/projects";
    private String authToken;
    private Long createdProjectId;
    private Long createdUserId;

    @BeforeEach
    public void setup() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Création utilisateur
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setUsername("testuser");
        testUser = userRepository.save(testUser);
        createdUserId = testUser.getId();

        // Création d'un projet
        Project project = new Project();
        project.setName("Projet Test");
        project.setDescription("Description du projet test");
        project.setStartDate(LocalDateTime.now());
        project.setCreatedBy(testUser);
        project = projectRepository.save(project);
        createdProjectId = project.getId();

        // Authentification
        AuthDto authDto = new AuthDto();
        authDto.setEmail("test@example.com");
        authDto.setPassword("password");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/pmt/users/login",
                authDto,
                String.class
        );

        authToken = response.getBody();
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        return headers;
    }

    @Test
    public void testCreateProject() {
        ProjectCreateDto projectDto = new ProjectCreateDto();
        projectDto.setName("Nouveau Projet");
        projectDto.setDescription("Description du nouveau projet");
        projectDto.setStartDate(LocalDate.now());

        HttpEntity<ProjectCreateDto> request = new HttpEntity<>(projectDto, getAuthHeaders());

        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/create",
                HttpMethod.POST,
                request,
                ProjectDto.class
        );

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(projectDto.getName(), response.getBody().getName());
        assertEquals(projectDto.getDescription(), response.getBody().getDescription());
    }

    @Test
    public void testGetAllProjects() {
        ResponseEntity<List<Project>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                new ParameterizedTypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetAllProjectsDto() {
        ResponseEntity<List<ProjectDto>> response = restTemplate.exchange(
                BASE_URL + "/details",
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                new ParameterizedTypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetProjectById() {
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                ProjectDto.class,
                createdProjectId
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdProjectId, response.getBody().getId());
    }

    @Test
    public void testUpdateProject() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Nom modifié");
        projectDto.setDescription("Nouvelle description");
        projectDto.setStartDate(LocalDate.now());

        HttpEntity<ProjectDto> request = new HttpEntity<>(projectDto, getAuthHeaders());

        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                ProjectDto.class,
                createdProjectId
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto.getName(), response.getBody().getName());
        assertEquals(projectDto.getDescription(), response.getBody().getDescription());
    }

    @Test
    public void testInviteMembres() {
        EmailRole emailRole = new EmailRole();
        emailRole.setEmail("test@example.com");
        emailRole.setRole(RoleName.MEMBER);

        List<EmailRole> emailRoles = List.of(emailRole);

        HttpEntity<List<EmailRole>> request = new HttpEntity<>(emailRoles, getAuthHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/invite/{projectId}",
                HttpMethod.POST,
                request,
                Void.class,
                createdProjectId
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
