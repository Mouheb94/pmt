package com.rendu.backend.controller.Test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rendu.backend.controleurs.TaskController;
import com.rendu.backend.enums.Priority;
import com.rendu.backend.models.Task;
import com.rendu.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Task task1;
    private Task task2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        task1 = new Task();
        task1.setId(1L);
        task1.setName("Fix login bug");
        task1.setDescription("Fix the issue with user login");
        task1.setDueDate(LocalDate.now().plusDays(3));
        task1.setPriority(Priority.valueOf("HIGH"));
        task2 = new Task();
        task2.setId(2L);
        task2.setName("Write documentation");
        task2.setDescription("Document the new API features");
        task2.setDueDate(LocalDate.now().plusDays(7));
        task2.setPriority(Priority.valueOf("MEDIUM"));
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task1);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fix login bug"));
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() throws Exception {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Fix login bug"))
                .andExpect(jsonPath("$[1].name").value("Write documentation"));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task1);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fix login bug"));
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        task1.setDescription("Updated description");
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(task1);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void deleteTask_shouldInvokeService() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(1L);
    }
}
