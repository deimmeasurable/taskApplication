package org.example.service;

import static javax.management.Query.times;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        // User 100 is Assigner, User 200 is Assignee
        sampleTask = new Task(1L, "Test Task", "High", "pending", 200L, 100L, LocalDateTime.now());
    }

    // --- RULE 1: CREATION ---
    @Test
    void createTask_ShouldSetStatusToPending() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task created = taskService.create(sampleTask);

        assertEquals("pending", created.getStatus());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    // --- RULE 2: ASSIGNER PERMISSIONS (Update/Delete) ---
    @Test
    void updateDetails_WhenUserIsAssigner_ShouldSucceed() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task updates = new Task();
        updates.setTitle("Updated Title");

        Task result = taskService.updateDetails(1L, 100L, updates); // 100 is Assigner

        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void updateDetails_WhenUserIsNotAssigner_ShouldThrowForbidden() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // Attempting update as User 200 (the Assignee, not the Assigner)
        assertThrows(ResponseStatusException.class, () -> {
            taskService.updateDetails(1L, 200L, new Task());
        });
    }

    @Test
    void delete_WhenUserIsNotAssigner_ShouldThrowForbidden() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        assertThrows(ResponseStatusException.class, () -> {
            taskService.delete(1L, 200L); // 200 is not the assigner
        });
    }

    @Test
    void updateStatus_WhenUserIsAssignee_ShouldSucceed() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.updateStatus(1L, 200L, "in-progress");

        assertEquals("in-progress", result.getStatus());
    }

    @Test
    void updateStatus_WhenUserIsAssignerButNotAssignee_ShouldThrowForbidden() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));


        assertThrows(ResponseStatusException.class, () -> {
            taskService.updateStatus(1L, 100L, "completed");
        });
    }


    @Test
    void getAllTasks_WithStatusFilter_ShouldCallCorrectRepoMethod() {
        taskService.getAllTasks(null, "completed");
        verify(taskRepository, times(1)).findByStatus("completed");
    }
}
