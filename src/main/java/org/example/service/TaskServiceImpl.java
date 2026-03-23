package org.example.service;

import com.sun.security.auth.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.domain.User;
import org.example.dto.TaskRequest;
import org.example.dto.UpdateTaskRequest;
import org.example.execptions.UnauthorizedActionException;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public abstract class TaskServiceImpl implements TaskService {
    @Autowired
    private final TaskRepository taskRepository;

  @Override
  @Transactional
  public Task createTask(TaskRequest taskRequest) {

      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if (!(principal instanceof User)) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
      }

      User currentUser = (User) principal;
      Long currentUserId = currentUser.getId();


      Task task = new Task();
      task.setTitle(taskRequest.getTitle());
      task.setPriority(taskRequest.getPriority());
      task.setAssignedTo(taskRequest.getAssignedTo());


      task.setAssignedBy(currentUserId);
      task.setStatus(Status.PENDING);
      task.setCreatedAt(LocalDateTime.now());

      return taskRepository.save(task);
  }



    @Transactional
    public Task updateTaskDetails(Long taskId, UpdateTaskRequest updateTaskRequest) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));


        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long currentUserId = currentUser.getId();


        if (!task.getAssignedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigner can edit details");
        }


        task.setTitle(updateTaskRequest.getTitle());
        task.setPriority(updateTaskRequest.getPriority());


        return taskRepository.save(task);
    }

    @Transactional
    @Override
    public Task updateTaskStatus(Long taskId, Status newStatus) {
        // 1. Fetch the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // 2. Extract the ID directly at the Service Level
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = ((User) principal).getId();


        if (task.getAssignedTo() == null || !task.getAssignedTo().equals(currentUserId)) {
            throw new UnauthorizedActionException(HttpStatus.FORBIDDEN,
                    "Access Denied: Only the assignee can update this task's status.");
        }

        // 4. Update and Save
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }



    @Override
    @Transactional
    public void deleteTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));


        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long currentUserId = currentUser.getId();

        if (!task.getAssignedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigner can delete this task");
        }


        taskRepository.delete(task);
    }


@Override
public Page<Task> getTasks(Long assignedTo, Status status, Pageable pageable) {
        if (assignedTo != null && status != null) {
            return taskRepository.findByAssignedToAndStatus(assignedTo, status, pageable);
        } else if (assignedTo != null) {
            return taskRepository.findByAssignedTo(assignedTo, pageable);
        } else if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }
        return taskRepository.findAll(pageable);
    }
@Override
public Page<Task> getAllTasks(Long assignedTo, Status status, Pageable pageable) {

        if (assignedTo != null && status != null) {
            return taskRepository.findByAssignedToAndStatus(assignedTo, status, pageable);
        }

        if (assignedTo != null) {
            return taskRepository.findByAssignedTo(assignedTo, pageable);
        }

        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }

        return taskRepository.findAll(pageable);
    }
    @Transactional
    @Override
    public Task unassignTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));


        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long currentUserId = currentUser.getId();

        if (!task.getAssignedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Unauthorized: Only the assigner can unassign this task.");
        }


        task.setAssignedTo(null);
        task.setStatus(Status.PENDING);

        return taskRepository.save(task);
    }
}





