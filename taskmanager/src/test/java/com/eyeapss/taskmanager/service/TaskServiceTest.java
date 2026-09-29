package com.eyeapss.taskmanager.service;

import com.eyeapss.taskmanager.dto.TaskRequest;
import com.eyeapss.taskmanager.dto.TaskResponse;
import com.eyeapss.taskmanager.entity.Task;
import com.eyeapss.taskmanager.entity.Task.Priority;
import com.eyeapss.taskmanager.entity.Task.Status;
import com.eyeapss.taskmanager.entity.User;
import com.eyeapss.taskmanager.repository.TaskRepository;
import com.eyeapss.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // enables Mockito annotations
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository; // fake — no real DB

    @Mock
    private UserRepository userRepository; // fake — no real DB

    @InjectMocks
    private TaskService taskService; // real TaskService with mocks injected

    private User testUser;
    private Task testTask;

    @BeforeEach // runs before each test — sets up fresh test data
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .role(User.Role.ROLE_USER)
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .user(testUser)
                .build();
    }

    @Test
    void getAllTasksForUser_returnsTaskList() {
        // ARRANGE — tell the mock what to return
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(testTask));

        // ACT — call the real method
        List<TaskResponse> result = taskService.getAllTasksForUser(1L);

        // ASSERT — verify the result
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        assertEquals(Status.TODO, result.get(0).getStatus());
    }

    @Test
    void getTaskById_returnsTask_whenOwnerRequests() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        TaskResponse result = taskService.getTaskById(1L, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void getTaskById_throwsException_whenTaskNotFound() {
        // tell mock to return empty — simulates "not in DB"
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // assertThrows verifies the exception IS thrown
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> taskService.getTaskById(99L, 1L));

        assertEquals("Task not found with id: 99", ex.getMessage());
    }

    @Test
    void getTaskById_throwsException_whenUserDoesNotOwnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // task belongs to user 1, but user 2 is requesting
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> taskService.getTaskById(1L, 2L));

        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void createTask_savesAndReturnsTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setStatus(Status.TODO);
        request.setPriority(Priority.MEDIUM);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse result = taskService.createTask(request, 1L);

        assertNotNull(result);
        // verify save() was called exactly once
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_deletesSuccessfully_whenOwnerRequests() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, 1L);

        // verify delete was called with the correct task
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void deleteTask_throwsException_whenUserDoesNotOwnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> taskService.deleteTask(1L, 2L));

        assertEquals("Access denied", ex.getMessage());
        // verify delete was NEVER called
        verify(taskRepository, never()).delete(any());
    }
}