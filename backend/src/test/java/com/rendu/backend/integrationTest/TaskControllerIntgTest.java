package com.rendu.backend.integrationTest;

import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.TaskRepository;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.dto.AuthDto;
import com.rendu.backend.enums.Priority;
import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Commit
public class TaskControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private static final String BASE_URL = "/pmt/tasks";
    private String authToken;

    private Long createdTaskId;

    @BeforeEach
    public void setup() {
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
    public void testCreateTask() {
        // Arrange
        Long projectId = 1L;
        Task task = new Task();
        task.setName("New Task");
        task.setDescription("New Task Description");
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setPriority(Priority.MEDIUM);
        task.setStatus(TaskStatus.TODO);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Task> request = new HttpEntity<>(task, headers);

        // Act
        ResponseEntity<Task> response = restTemplate.exchange(
                BASE_URL + "/create/{projectId}",
                HttpMethod.POST,
                request,
                Task.class,
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(task.getName(), response.getBody().getName());
        assertEquals(task.getDescription(), response.getBody().getDescription());
        assertEquals(task.getPriority(), response.getBody().getPriority());
        assertEquals(task.getStatus(), response.getBody().getStatus());
    }
    @Test
    public void testGetAllTasks() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<Task>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<Task>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetAllTasksByProjectId() {
        // Arrange
        Long projectId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<List<Task>> response = restTemplate.exchange(
                BASE_URL + "/project/{projectId}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<Task>>() {},
                projectId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetTaskById() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<Task> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.GET,
                request,
                Task.class,
                createdTaskId // Utilise l'ID de la tâche créée en setup()
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdTaskId, response.getBody().getId());
    }



    @Test
    public void testAssigneTask() {
        // Arrange
        Long userId = 1L;
        Long taskId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<Task> response = restTemplate.exchange(
                BASE_URL + "/assigner/{userId}/{taskId}",
                HttpMethod.PATCH,
                null,
                Task.class,
                userId, taskId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
        assertNotNull(response.getBody().getAssignedTo());
        assertEquals(userId, response.getBody().getAssignedTo().getId());
    }

    @Test
    public void testUpdateTask() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;
        
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task Name");
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setPriority(Priority.HIGH);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Task> request = new HttpEntity<>(updatedTask, headers);

        // Act
        ResponseEntity<Task> response = restTemplate.exchange(
                BASE_URL + "/{idTask}/{idUser}",
                HttpMethod.PUT,
                request,
                Task.class,
                taskId, userId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
        assertEquals(updatedTask.getName(), response.getBody().getName());
        assertEquals(updatedTask.getDescription(), response.getBody().getDescription());
        assertEquals(updatedTask.getPriority(), response.getBody().getPriority());
        assertEquals(updatedTask.getStatus(), response.getBody().getStatus());
    }
} 