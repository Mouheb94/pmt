package com.rendu.backend.controller.Test;

import com.rendu.backend.controleurs.RoleController;
import com.rendu.backend.models.Role;
import com.rendu.backend.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
    }

    @Test
    void getAllRoles_shouldReturnListOfRoles() throws Exception {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "MEMBER");

        List<Role> roles = Arrays.asList(role1, role2);

        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("MEMBER"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(roleService, times(1)).getAllRoles();
    }
}
