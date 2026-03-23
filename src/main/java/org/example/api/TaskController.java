package org.example.api;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.example.domain.Status;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
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
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        TaskResponse task= service.createTask(request);
        return ResponseEntity.ok(task);
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
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        TaskResponse updatedTask = service.updateTaskDetails(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    // 4. Update Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        TaskResponse response= service.updateTaskStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/unassign")
    public ResponseEntity<TaskResponse> unassignTask(
            @PathVariable Long id){
        TaskResponse response= service.unassignTask(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) Status status,
            Pageable pageable) {

        return ResponseEntity.ok(service.getAllTasks(assignedTo, status, pageable));

    }
        @DeleteMapping("/{id}")
        public ResponseEntity<TaskResponse> deleteTask(@PathVariable Long id) {
            TaskResponse response=service.deleteTask(id);
            return ResponseEntity.ok(response);

        }
}
