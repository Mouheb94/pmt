package com.rendu.backend.controller.Test;



import com.rendu.backend.controleurs.NotificationController;
import com.rendu.backend.models.Notification;
import com.rendu.backend.models.User;
import com.rendu.backend.service.NotificationService;
import com.rendu.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        // Initialisation de base
        testUser = new User();
        testUser.setId(1L);

        testNotification = new Notification("Test message", testUser);
        testNotification.setId(1L);
    }

    @Test
    void sendNotification_shouldReturnNotification() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(notificationService.sendNotification(anyString(), eq(testUser)))
                .thenReturn(testNotification);

        mockMvc.perform(post("/api/notifications/send/1")
                        .content("Test message")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test message"))
                .andExpect(jsonPath("$.recipient.id").value(1));
    }

    @Test
    void getUserNotifications_shouldReturnList() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(notificationService.getUserNotifications(testUser)).thenReturn(List.of(testNotification));

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is("Test message")));
    }

    @Test
    void markAsRead_shouldCallService() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(1L);
    }
}

