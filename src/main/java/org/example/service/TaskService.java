package org.example.service;

import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;

import java.util.List;

public interface TaskService {
    public Task createTask(TaskRequest request);
    public List<Task> getTasks(Long assignedTo, Status status);
    public Task updateTask(Long taskId, TaskRequest request, Long userId);
    public Task updateStatus(Long taskId, Status status, Long userId);
    public Task unassignTask(Long taskId, Long userId);
    public void deleteTask(Long taskId, Long userId);
}
