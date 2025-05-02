package com.rendu.backend.integrationTest;

import com.rendu.backend.dao.*;
import com.rendu.backend.dto.AuthDto;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.Role;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-Test.properties")
@Transactional
public class ProjectMemberControllerIntgTest {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken;
    private Long createdProjectId;
    private Long createdUserId;

    private static final String BASE_URL = "/api/roles";

    @BeforeEach
    public void setup() {
        // Nettoyage
        taskRepository.deleteAll();
        projectMemberRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Rôles
        roleRepository.save(new Role(RoleName.ADMIN));
        roleRepository.save(new Role(RoleName.MEMBER));
        roleRepository.save(new Role(RoleName.OBSERVATOR));

        // Utilisateur
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user = userRepository.save(user);
        createdUserId = user.getId();

        // Authentification
        AuthDto login = new AuthDto();
        login.setEmail("test@example.com");
        login.setPassword("password");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/pmt/users/login", login, String.class);
        authToken = loginResponse.getBody();

        // Projet
        Project project = new Project();
        project.setName("Projet Test");
        project.setDescription("Description du projet test");
        project.setStartDate(LocalDateTime.now());
        project.setCreatedBy(user);
        project = projectRepository.save(project);
        createdProjectId = project.getId();

        // Lier utilisateur au projet (pour testGetProjectsByUser par exemple)
        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProject(project);
        member.setRole(RoleName.MEMBER);
        projectMemberRepository.save(member);
    }

    private HttpHeaders authorizedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return headers;
    }

    @Test
    public void testAddMember() {
        ProjectMember newMember = new ProjectMember();
        newMember.setUser(userRepository.findById(createdUserId).orElseThrow());
        newMember.setProject(projectRepository.findById(createdProjectId).orElseThrow());
        newMember.setRole(RoleName.MEMBER);

        HttpEntity<ProjectMember> request = new HttpEntity<>(newMember, authorizedHeaders());

        ResponseEntity<ProjectMember> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                ProjectMember.class
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RoleName.MEMBER, response.getBody().getRole());
        assertEquals(createdProjectId, response.getBody().getProject().getId());
        assertEquals(createdUserId, response.getBody().getUser().getId());
    }

    @Test
    public void testGetMembersByProject() {
        HttpEntity<Void> request = new HttpEntity<>(authorizedHeaders());

        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/{projectId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                createdProjectId
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        response.getBody().forEach(member -> {
            assertEquals(createdProjectId, member.getProject().getId());
            assertNotNull(member.getUser());
            assertNotNull(member.getRole());
        });
    }

    @Test
    public void testGetProjectsByUser() {
        HttpEntity<Void> request = new HttpEntity<>(authorizedHeaders());

        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/user/{userId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                createdUserId
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        response.getBody().forEach(pm -> {
            assertEquals(createdUserId, pm.getUser().getId());
            assertNotNull(pm.getProject());
            assertNotNull(pm.getRole());
        });
    }

    @Test
    public void testGetMembersByProject_NoMembers() {
        // Créer un projet sans membres
        Project project = new Project();
        project.setName("Empty Project");
        project.setDescription("Sans membres");
        project.setStartDate(LocalDateTime.now());
        project.setCreatedBy(userRepository.findById(createdUserId).orElseThrow());
        project = projectRepository.save(project);

        HttpEntity<Void> request = new HttpEntity<>(authorizedHeaders());

        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/{projectId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                project.getId()
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testGetProjectsByUser_NoProjects() {
        // Créer un utilisateur sans projet
        User noProjectUser = new User();
        noProjectUser.setEmail("noproj@example.com");
        noProjectUser.setUsername("noproj");
        noProjectUser.setPassword(passwordEncoder.encode("password"));
        noProjectUser = userRepository.save(noProjectUser);

        HttpEntity<Void> request = new HttpEntity<>(authorizedHeaders());

        ResponseEntity<List<ProjectMember>> response = restTemplate.exchange(
                BASE_URL + "/user/{userId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ProjectMember>>() {},
                noProjectUser.getId()
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
