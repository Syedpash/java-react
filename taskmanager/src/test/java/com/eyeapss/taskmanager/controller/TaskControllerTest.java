package com.eyeapss.taskmanager.controller;

import com.eyeapss.taskmanager.dto.TaskRequest;
import com.eyeapss.taskmanager.dto.TaskResponse;
import com.eyeapss.taskmanager.entity.Task.Priority;
import com.eyeapss.taskmanager.entity.Task.Status;
import com.eyeapss.taskmanager.entity.User;
import com.eyeapss.taskmanager.repository.UserRepository;
import com.eyeapss.taskmanager.security.CustomUserDetailsService;
import com.eyeapss.taskmanager.security.JwtUtil;
import com.eyeapss.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class) // only loads TaskController + security layer
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc; // simulates HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // converts objects to/from JSON

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;
    private TaskResponse testTaskResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded")
                .role(User.Role.ROLE_USER)
                .build();

        testTaskResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "testuser") // simulates authenticated user
    void getAllTasks_returnsTaskList() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(taskService.getAllTasksForUser(1L)).thenReturn(List.of(testTaskResponse));

        mockMvc.perform(get("/tasks")) // simulate GET /tasks
                .andExpect(status().isOk()) // assert HTTP 200
                .andExpect(jsonPath("$[0].title").value("Test Task")) // assert JSON body
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTaskById_returnsTask() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(taskService.getTaskById(1L, 1L)).thenReturn(testTaskResponse);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTask_returnsCreatedTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Description");
        request.setStatus(Status.TODO);
        request.setPriority(Priority.HIGH);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(taskService.createTask(any(TaskRequest.class), eq(1L))).thenReturn(testTaskResponse);

        mockMvc.perform(post("/tasks")
                .with(csrf()) // CSRF token required for POST in tests
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) // serialize request to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTask_returns204() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/tasks/1").with(csrf()))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    void getAllTasks_returns401_whenNotAuthenticated() throws Exception {
        // no @WithMockUser — unauthenticated request
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized()); // 401
    }
}