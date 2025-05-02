package com.rendu.backend.integrationTest;

import com.rendu.backend.dao.*;
import com.rendu.backend.dto.AuthDto;
import com.rendu.backend.enums.Priority;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-Test.properties")
@Commit
public class RoleControllerIntgTest {

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
    private TestRestTemplate restTemplate;  // Injection de TestRestTemplate

    private String authToken;
    private Long createdTaskId;
    private static Long createdProjectId;

    private static final String BASE_URL = "/api/roles";

    @BeforeEach
    public void setup() {
        // Nettoyage des données
        taskRepository.deleteAll();
        projectMemberRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Création des rôles
        Role adminRole = new Role(RoleName.ADMIN);
        Role memberRole = new Role(RoleName.MEMBER);
        Role observatorRole = new Role(RoleName.OBSERVATOR);
        roleRepository.save(adminRole);
        roleRepository.save(memberRole);
        roleRepository.save(observatorRole);

        // Création de l'utilisateur
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setUsername("testuser");
        testUser = userRepository.save(testUser);

        // Création du projet
        Project project = new Project();
        project.setName("Projet Test");
        project.setDescription("Description du projet test");
        project.setStartDate(LocalDateTime.now());
        project.setCreatedBy(testUser);
        project = projectRepository.save(project);
        createdProjectId=project.getId();

        // Création du membre du projet avec le rôle ADMIN
        ProjectMember projectMember = new ProjectMember(testUser, project, RoleName.ADMIN);
        projectMemberRepository.save(projectMember);

        // Création d'une tâche
        Task task = new Task();
        task.setProject(project);
        task.setPriority(Priority.MEDIUM);
        task.setName("Tâche test");
        task.setDescription("Description test");
        task.setCreatedBy(testUser);
        task = taskRepository.save(task);
        createdTaskId = task.getId();

        // Authentification
        AuthDto authDto = new AuthDto();
        authDto.setEmail("test@example.com");
        authDto.setPassword("password");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/pmt/users/login",
                authDto,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode(), "La connexion devrait réussir");
        assertNotNull(response.getBody(), "Le token ne devrait pas être null");
        authToken = response.getBody();
    }


    @Test
    public void testGetAllRoles() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<Role>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<Role>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        List<Role> roles = response.getBody();
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleName.ADMIN));
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleName.MEMBER));
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleName.OBSERVATOR));
    }

    @Test
    public void testGetAllRoles_EmptyList() {
        // Arrange: suppression explicite des rôles
        roleRepository.deleteAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<Role>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<Role>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
