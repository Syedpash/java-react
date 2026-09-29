package com.eyeapss.taskmanager.repository;

import com.eyeapss.taskmanager.entity.Task;
import com.eyeapss.taskmanager.entity.Task.Status;
import com.eyeapss.taskmanager.entity.Task.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository 
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Status status);

    List<Task> findByUserIdAndPriority(Long userId, Priority priority);
}