/*package com.rendu.backend.controller.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rendu.backend.controleurs.ProjectController;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.models.Project;
import com.rendu.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private Project project1;
    private Project project2;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        project1 = new Project();
        project1.setId(1L);
        project1.setName("Project Alpha");

        project2 = new Project();
        project2.setId(2L);
        project2.setName("Project Beta");
    }

    @Test
    void createProject_shouldReturnCreatedProject() throws Exception {
        when(projectService.createProject(any(com.rendu.backend.dto.ProjectDto.class)));

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Project Alpha"));
    }

    @Test
    void getAllProjects_shouldReturnProjectList() throws Exception {
        List<Project> projects = Arrays.asList(project1, project2);
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Project Alpha")))
                .andExpect(jsonPath("$[1].name", is("Project Beta")));
    }

    @Test
    void getProjectById_shouldReturnProject() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(project1);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project Alpha"));
    }

    @Test
    void updateProject_shouldReturnUpdatedProject() throws Exception {
        project1.setDescription("Updated description");
        when(projectService.updateProject(eq(1L), any(ProjectDto.class))).thenReturn(project1);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Updated description")));
    }

    @Test
    void deleteProject_shouldCallService() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk());

        verify(projectService, times(1)).deleteProject(1L);
    }
}
