package org.example.service;

import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.dto.UpdateTaskRequest;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static javax.management.Query.times;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskRequest sampleTask;

    @BeforeEach
//    void setUp() {
//        // User 100 is Assigner, User 200 is Assignee
//        sampleTask = new Task(1L, "Test Task", "High", "pending", 200L, 100L, LocalDateTime.now());
//    }


    @Test
    void createTask_ShouldSetStatusToPending() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse created = taskService.createTask(sampleTask);

        assertEquals("pending", created);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    // --- RULE 2: ASSIGNER PERMISSIONS (Update/Delete) ---
    @Test
    void updateDetails_WhenUserIsAssigner_ShouldSucceed() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        UpdateTaskRequest updates = new UpdateTaskRequest();
        updates.setTitle("Updated Title");

        Task result = taskService.updateTaskDetails(1L,  updates);

        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void updateDetails_WhenUserIsNotAssigner_ShouldThrowForbidden() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));


        assertThrows(ResponseStatusException.class, () -> {
            taskService.updateTaskDetails(1L, UpdateTaskRequest.builder().build());
        });
    }

    @Test
    void delete_WhenUserIsNotAssigner_ShouldThrowForbidden() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        assertThrows(ResponseStatusException.class, () -> {
            taskService.deleteTask(1L);
        });
    }

    @Test
    void updateStatus_WhenUserIsAssignee_ShouldSucceed() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.updateTaskStatus(1L,  Status.IN_PROGRESS);

        assertEquals("in-progress", result.getStatus());
    }

    @Test
    void updateStatus_WhenUserIsAssignerButNotAssignee_ShouldThrowForbidden() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));


        assertThrows(ResponseStatusException.class, () -> {
            taskService.updateTaskStatus(1L,  Status.COMPLETED);
        });
    }


    @Test
    void getAllTasks_WithStatusFilter_ShouldCallCorrectRepoMethod() {
        taskService.getAllTasks(null, Status.valueOf("completed"), Pageable.ofSize(1));
        verify(taskRepository, times(1)).findByStatus("completed");
    }
}
