package com.rendu.backend.controller.Test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rendu.backend.controleurs.ProjectMemberController;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.User;
import com.rendu.backend.service.ProjectMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectMemberController.class)
class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProjectMemberService projectMemberService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectMember sampleMember;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(1L);

        sampleMember = new ProjectMember(user, project, RoleName.MEMBER);
    }

    @Test
    void testAddMember() throws Exception {
        when(projectMemberService.addMemberToProject(any(ProjectMember.class)))
                .thenReturn(sampleMember);

        mockMvc.perform(post("/api/project-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void testGetMembersByProject() throws Exception {
        when(projectMemberService.getMembersByProject(1L))
                .thenReturn(List.of(sampleMember));

        mockMvc.perform(get("/api/project-members/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProjectsByUser() throws Exception {
        when(projectMemberService.getProjectsByUser(1L))
                .thenReturn(List.of(sampleMember));

        mockMvc.perform(get("/api/project-members/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testRemoveMember() throws Exception {
        doNothing().when(projectMemberService).removeMember(1L);

        mockMvc.perform(delete("/api/project-members/1"))
                .andExpect(status().isOk());
    }
}

