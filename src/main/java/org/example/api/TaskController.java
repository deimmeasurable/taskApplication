package org.example.api;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.dto.UpdateTaskRequest;
import org.example.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return service.createTask(request);
    }


    @GetMapping
    public ResponseEntity<Page<Task>> getTask(
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Task> tasks = service.getTasks(assignedTo, status, pageable);
        return ResponseEntity.ok(tasks);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {

        // In a real JWT setup, use: SecurityUtils.getCurrentUserId()
        // For now, if you are still testing manually, you can keep the userId parameter
        Task updatedTask = service.updateTaskDetails(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    // 4. Update Status
    @PatchMapping("/{id}/status")
    public Task updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        return service.updateTaskStatus(id, status);
    }

    // 5. Unassign Task
    @PatchMapping("/{id}/unassign")
    public Task unassignTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return service.unassignTask(id, userId);
    }

    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) Status status,
            Pageable pageable) {

        return ResponseEntity.ok(service.getAllTasks(assignedTo, status, pageable));

    }
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
            service.deleteTask(id);
            return ResponseEntity.noContent().build(); // Returns 204 No Content on success
        }
}
