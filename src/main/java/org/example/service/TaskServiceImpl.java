package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.execptions.UnauthorizedActionException;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title);
        task.setPriority(request.priority);
        task.setAssignedTo(request.assignedTo);
        task.setAssignedBy(request.assignedBy);
        task.setStatus(Status.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }


    public List<Task> getTasks(Long assignedTo, Status status) {
        if (assignedTo != null) return taskRepository.findByAssignedTo(assignedTo);
        if (status != null) return taskRepository.findByStatus(status);
        return taskRepository.findAll();
    }
    @Transactional
    public Task updateTask(Long taskId, TaskRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedBy().equals(userId)) {
            throw new UnauthorizedActionException("Only assigner can update task");
        }

        task.setTitle(request.title);
        task.setPriority(request.priority);
        task.setAssignedTo(request.assignedTo);

        return taskRepository.save(task);
    }


    public Task updateStatus(Long taskId, Status status, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedTo().equals(userId)) {
            throw new UnauthorizedActionException("Only assignee can update status");
        }

        task.setStatus(status);
        return taskRepository.save(task);
    }


    public Task unassignTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedBy().equals(userId)) {
            throw new UnauthorizedActionException("Only assigner can unassign");
        }

        task.setAssignedTo(null);
        return taskRepository.save(task);
    }


    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedBy().equals(userId)) {
            throw new UnauthorizedActionException("Only assigner can delete");
        }

        taskRepository.delete(task);
    }
    public List<Task> getAllTasks(Long assignedTo, Status status) {
        if (assignedTo != null) return taskRepository.findByAssignedTo(assignedTo);
        if (status != null) return taskRepository.findByStatus(status);
        return taskRepository.findAll();
    }
}



