package com.rendu.backend.controller.Test;


import com.rendu.backend.controleurs.TaskHistoryController;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.TaskHistory;
import com.rendu.backend.service.TaskHistoryService;
import com.rendu.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskHistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskHistoryService taskHistoryService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskHistoryController taskHistoryController;

    private Task task;
    private TaskHistory history1, history2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskHistoryController).build();

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");

        history1 = new TaskHistory(task, null, "name", "Old Name", "New Name");
        history1.setId(1L);
        history1.setModificationDate(LocalDateTime.now());

        history2 = new TaskHistory(task, null, "description", "Old desc", "New desc");
        history2.setId(2L);
        history2.setModificationDate(LocalDateTime.now());
    }

    @Test
    void getHistoryByTask_shouldReturnListOfTaskHistory() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task);
        when(taskHistoryService.getHistoryForTask(task)).thenReturn(Arrays.asList(history1, history2));

        mockMvc.perform(get("/api/task-history/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fieldChanged").value("name"))
                .andExpect(jsonPath("$[1].fieldChanged").value("description"));

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskHistoryService, times(1)).getHistoryForTask(task);
    }
}

