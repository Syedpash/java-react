package com.eyeapss.taskmanager.controller;

import com.eyeapss.taskmanager.dto.TaskRequest;
import com.eyeapss.taskmanager.dto.TaskResponse;
import com.eyeapss.taskmanager.entity.User;
import com.eyeapss.taskmanager.repository.UserRepository;
import com.eyeapss.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    // GET /tasks — get all tasks for the logged-in user
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(taskService.getAllTasksForUser(userId));
    }

    // GET /tasks/5 — get one task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id, Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(taskService.getTaskById(id, userId));
    }

    // POST /tasks — create a new task
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request,
            Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(taskService.createTask(request, userId));
    }

    // PUT /tasks/5 — update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
            @RequestBody TaskRequest request,
            Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(taskService.updateTask(id, request, userId));
    }

    // DELETE /tasks/5 — delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        Long userId = getUserId(principal);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // helper — gets the DB user ID from the JWT username
    private Long getUserId(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}