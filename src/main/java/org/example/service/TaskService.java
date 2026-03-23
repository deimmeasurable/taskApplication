package org.example.service;

import jakarta.transaction.Transactional;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.dto.UpdateTaskRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest request);
    TaskResponse updateTaskDetails(Long taskId, UpdateTaskRequest updateTaskRequest);
    TaskResponse updateTaskStatus(Long taskId, Status newStatus);
    TaskResponse deleteTask(Long taskId);
    Page<Task> getTasks(Long assignedTo, Status status, Pageable pageable);
    Page<Task> getAllTasks(Long assignedTo, Status status, Pageable pageable);
    TaskResponse unassignTask(Long taskId);
}
