package com.eyeapss.taskmanager.dto;

import com.eyeapss.taskmanager.entity.Task.Priority;
import com.eyeapss.taskmanager.entity.Task.Status;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
