package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.TaskController;
import com.rendu.backend.enums.Priority;
import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Task;
import com.rendu.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        // Arrange
        Long projectId = 1L;
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setDueDate(LocalDate.now());
        task.setPriority(Priority.MEDIUM);
        task.setStatus(TaskStatus.TODO);

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setDueDate(task.getDueDate());
        savedTask.setPriority(task.getPriority());
        savedTask.setStatus(task.getStatus());

        when(taskService.createTask(any(Task.class), anyLong())).thenReturn(savedTask);

        // Act
        Task result = taskController.createTask(task, projectId);

        // Assert
        assertNotNull(result);
        assertEquals(savedTask.getId(), result.getId());
        assertEquals(savedTask.getName(), result.getName());
        verify(taskService, times(1)).createTask(any(Task.class), eq(projectId));
    }

    @Test
    void testGetAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("Task 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Task 2");

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // Act
        List<Task> result = taskController.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tasks, result);
        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testGetAllTasksByProjectId() {
        // Arrange
        Long projectId = 1L;
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("Task 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Task 2");

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasksByProjectId(projectId)).thenReturn(tasks);

        // Act
        List<Task> result = taskController.getAllTasksByProjectId(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tasks, result);
        verify(taskService, times(1)).getAllTasksByProjectId(projectId);
    }

    @Test
    void testGetTaskById() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("Test Task");

        when(taskService.getTaskById(taskId)).thenReturn(task);

        // Act
        Task result = taskController.getTaskById(taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(task.getName(), result.getName());
        verify(taskService, times(1)).getTaskById(taskId);
    }



    @Test
    void testAssigneTask() {
        // Arrange
        Long userId = 1L;
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("Test Task");

        when(taskService.assigneTask(userId, taskId)).thenReturn(task);

        // Act
        Task result = taskController.assigneTask(userId, taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(task.getName(), result.getName());
        verify(taskService, times(1)).assigneTask(userId, taskId);
    }

    @Test
    void testUpdateTask() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setPriority(Priority.HIGH);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setName(updatedTask.getName());
        savedTask.setDescription(updatedTask.getDescription());
        savedTask.setPriority(updatedTask.getPriority());
        savedTask.setStatus(updatedTask.getStatus());

        when(taskService.updateTask(taskId, userId, updatedTask)).thenReturn(savedTask);

        // Act
        Task result = taskController.updateTask(taskId, userId, updatedTask);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(updatedTask.getName(), result.getName());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        assertEquals(updatedTask.getPriority(), result.getPriority());
        assertEquals(updatedTask.getStatus(), result.getStatus());
        verify(taskService, times(1)).updateTask(taskId, userId, updatedTask);
    }
} 