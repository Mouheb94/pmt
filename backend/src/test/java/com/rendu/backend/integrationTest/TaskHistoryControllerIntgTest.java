package com.rendu.backend.integrationTest;

import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.TaskHistoryRepository;
import com.rendu.backend.dao.TaskRepository;
import com.rendu.backend.dto.TaskHistoryDto;
import com.rendu.backend.enums.Priority;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import com.rendu.backend.models.User;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.dto.AuthDto;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Commit
public class TaskHistoryControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Long createdTaskId;

    private static final String BASE_URL = "/pmt/task-history";

    @BeforeEach
    void setUp() {
        // Nettoyage
        taskHistoryRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Création utilisateur
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setUsername("testuser");
        userRepository.save(testUser);

        // Création d'un projet
        Project project = new Project();
        project.setName("Projet Test");
        project.setDescription("Description du projet test");
        project.setStartDate(LocalDateTime.now());
        project.setCreatedBy(testUser);
        projectRepository.save(project);


        // Création tâche
        Task task = new Task();
        task.setProject(project);
        task.setPriority(Priority.MEDIUM);
        task.setName("Tâche test");
        task.setDescription("Description test");
        task.setCreatedBy(testUser);
        task = taskRepository.save(task);
        createdTaskId = task.getId(); // pour le test

        // Création historique
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setFieldChanged("description");
        history.setOldValue("Old desc");
        history.setNewValue("New desc");
        history.setModificationDate(LocalDateTime.now());
        history.setModifiedBy(testUser);
        taskHistoryRepository.save(history);

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

    @Test
    public void testGetHistoryByTask_WithHistory() {
        // Arrange
        Long taskId = 1L; // ID de la tâche de test
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<TaskHistoryDto>> response = restTemplate.exchange(
                BASE_URL + "/{taskId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<TaskHistoryDto>>() {},
                taskId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Vérification des données de l'historique
        TaskHistoryDto historyDto = response.getBody().get(0);
        assertNotNull(historyDto.getFieldChanged());
        assertNotNull(historyDto.getOldValue());
        assertNotNull(historyDto.getNewValue());
        assertNotNull(historyDto.getModificationDate());
        assertNotNull(historyDto.getModifiedBy());
    }

    @Test
    public void testGetHistoryByTask_NoHistory() {
        // Arrange
        Long taskId = 999L; // ID d'une tâche inexistante
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<TaskHistoryDto>> response = restTemplate.exchange(
                BASE_URL + "/{taskId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<TaskHistoryDto>>() {},
                taskId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testGetHistoryByTask_Unauthorized() {
        // Arrange
        Long taskId = 1L;
        HttpHeaders headers = new HttpHeaders();
        // Pas de token d'authentification
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<TaskHistoryDto>> response = restTemplate.exchange(
                BASE_URL + "/{taskId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<TaskHistoryDto>>() {},
                taskId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
} 