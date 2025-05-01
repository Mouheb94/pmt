package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.RoleController;
import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Role;
import com.rendu.backend.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName(RoleName.ADMIN);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(RoleName.MEMBER);

        Role role3 = new Role();
        role3.setId(3L);
        role3.setName(RoleName.OBSERVATOR);

        List<Role> roles = Arrays.asList(role1, role2, role3);
        when(roleService.getAllRoles()).thenReturn(roles);

        // Act
        List<Role> result = roleController.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(roles, result);
        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void testGetAllRoles_EmptyList() {
        // Arrange
        List<Role> emptyRoles = List.of();
        when(roleService.getAllRoles()).thenReturn(emptyRoles);

        // Act
        List<Role> result = roleController.getAllRoles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleService, times(1)).getAllRoles();
    }
} 