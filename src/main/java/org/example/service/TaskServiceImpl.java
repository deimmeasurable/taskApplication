package org.example.service;

import com.sun.security.auth.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.domain.User;
import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
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
  public TaskResponse createTask(TaskRequest taskRequest) {

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
      taskRepository.save(task);
      return TaskResponse.builder()
              .message("task created successfully")
              .build();

  }



    @Transactional
    public TaskResponse updateTaskDetails(Long taskId, UpdateTaskRequest updateTaskRequest) {

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

        taskRepository.save(task);
        return TaskResponse.builder()
                .message("task updated successfully")
                .build();
    }

    @Transactional
    @Override
    public TaskResponse updateTaskStatus(Long taskId, Status newStatus) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = ((User) principal).getId();


        if (task.getAssignedTo() == null || !task.getAssignedTo().equals(currentUserId)) {
            throw new UnauthorizedActionException(HttpStatus.FORBIDDEN,
                    "Access Denied: Only the assignee can update this task's status.");
        }

        task.setStatus(newStatus);
        taskRepository.save(task);

        return TaskResponse.builder()
                .message("task status updated successfully")
                .build();
    }



    @Override
    @Transactional
    public TaskResponse deleteTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));


        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long currentUserId = currentUser.getId();

        if (!task.getAssignedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigner can delete this task");
        }


        taskRepository.delete(task);
        return TaskResponse.builder()
                .message("task  delete successfully")
                .build();

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
    public TaskResponse unassignTask(Long taskId) {

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

        taskRepository.save(task);
        return TaskResponse.builder()
                .message("unassignTask successfully")
                .build();

    }
}





