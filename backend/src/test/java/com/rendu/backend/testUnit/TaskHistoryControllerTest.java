package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.TaskHistoryController;
import com.rendu.backend.dto.TaskHistoryDto;
import com.rendu.backend.service.TaskHistoryService;
import com.rendu.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskHistoryControllerTest {

    @Mock
    private TaskHistoryService taskHistoryService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskHistoryController taskHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetHistoryByTask() {
        // Arrange
        Long taskId = 1L;
        
        TaskHistoryDto historyDto1 = new TaskHistoryDto(
            "status",
            "TODO",
            "IN_PROGRESS",
            LocalDateTime.now(),
            "user1"
        );
        
        TaskHistoryDto historyDto2 = new TaskHistoryDto(
            "priority",
            "LOW",
            "HIGH",
            LocalDateTime.now(),
            "user2"
        );
        
        List<TaskHistoryDto> expectedHistory = Arrays.asList(historyDto1, historyDto2);
        
        when(taskHistoryService.getHistoryForTask(taskId)).thenReturn(expectedHistory);

        // Act
        ResponseEntity<List<TaskHistoryDto>> response = taskHistoryController.getHistoryByTask(taskId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedHistory, response.getBody());
        
        verify(taskHistoryService, times(1)).getHistoryForTask(taskId);
    }

    @Test
    void testGetHistoryByTask_EmptyHistory() {
        // Arrange
        Long taskId = 1L;
        List<TaskHistoryDto> emptyHistory = List.of();
        
        when(taskHistoryService.getHistoryForTask(taskId)).thenReturn(emptyHistory);

        // Act
        ResponseEntity<List<TaskHistoryDto>> response = taskHistoryController.getHistoryByTask(taskId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        
        verify(taskHistoryService, times(1)).getHistoryForTask(taskId);
    }
} 