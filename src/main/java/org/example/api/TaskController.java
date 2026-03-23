package org.example.api;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.example.domain.Task;
import org.example.dto.TaskRequest;
import org.example.service.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // 1. Create Task
    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return service.createTask(request);
    }

    // 2. Get Tasks
    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) Task.Status status) {
        return service.getTasks(assignedTo, status);
    }

    // 3. Update Task
    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            @RequestParam Long userId) {
        return service.updateTask(id, request, userId);
    }

    // 4. Update Status
    @PatchMapping("/{id}/status")
    public Task updateStatus(
            @PathVariable Long id,
            @RequestParam Task.Status status,
            @RequestParam Long userId) {
        return service.updateStatus(id, status, userId);
    }

    // 5. Unassign Task
    @PatchMapping("/{id}/unassign")
    public Task unassignTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return service.unassignTask(id, userId);
    }

    // 6. Delete Task
    @DeleteMapping("/{id}")
    public void deleteTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        service.deleteTask(id, userId);
    }
}
