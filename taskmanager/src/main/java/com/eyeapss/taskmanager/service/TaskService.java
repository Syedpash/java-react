package com.eyeapss.taskmanager.service;

import com.eyeapss.taskmanager.dto.TaskRequest;
import com.eyeapss.taskmanager.dto.TaskResponse;
import com.eyeapss.taskmanager.entity.Task;
import com.eyeapss.taskmanager.entity.Task.Status;
import com.eyeapss.taskmanager.entity.User;
import com.eyeapss.taskmanager.repository.TaskRepository;
import com.eyeapss.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<TaskResponse> getAllTasksForUser(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return toResponse(task);
    }

    public TaskResponse createTask(TaskRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Status.TODO)
                .priority(request.getPriority())
                .user(user)
                .build();

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long taskId, TaskRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());

        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .userId(task.getUser().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}