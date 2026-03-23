package org.example.service;

import jakarta.transaction.Transactional;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.dto.UpdateTaskRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
     Task createTask(TaskRequest request);
    Task updateTaskDetails(Long taskId, UpdateTaskRequest updateTaskRequest);
    Task updateTaskStatus(Long taskId, Status newStatus);
     Task unassignTask(Long taskId, Long userId);
    void deleteTask(Long taskId);
    Page<Task> getTasks(Long assignedTo, Status status, Pageable pageable);
    Page<Task> getAllTasks(Long assignedTo, Status status, Pageable pageable);

}
