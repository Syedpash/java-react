package com.eyeapss.taskmanager.dto;

import com.eyeapss.taskmanager.entity.Task.Priority;
import com.eyeapss.taskmanager.entity.Task.Status;
import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
}