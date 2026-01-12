package at.geise.test.springboot4test.integration;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.repository.TaskRepository;
import at.geise.test.springboot4test.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TaskIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        // Given
        TaskDto dto = new TaskDto(
                null,
                "Integration Test Task",
                "Description",
                Task.Priority.HIGH,
                Task.Status.TODO,
                LocalDateTime.now().plusDays(1)
        );

        // When
        Task created = taskService.create(dto);

        // Then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Integration Test Task");
        assertThat(created.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(created.getStatus()).isEqualTo(Task.Status.TODO);
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRetrieveTaskById() {
        // Given
        TaskDto dto = new TaskDto(null, "Test Task", "Desc", Task.Priority.MEDIUM, Task.Status.TODO, null);
        Task created = taskService.create(dto);

        // When
        Task retrieved = taskService.get(created.getId());

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(created.getId());
        assertThat(retrieved.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        // Given
        TaskDto createDto = new TaskDto(null, "Original", "Desc", Task.Priority.LOW, Task.Status.TODO, null);
        Task created = taskService.create(createDto);

        TaskDto updateDto = new TaskDto(
                created.getId(),
                "Updated Title",
                "Updated Description",
                Task.Priority.HIGH,
                Task.Status.DONE,
                LocalDateTime.now().plusDays(2)
        );

        // When
        Task updated = taskService.update(created.getId(), updateDto);

        // Then
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(updated.getStatus()).isEqualTo(Task.Status.DONE);
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteTask() {
        // Given
        TaskDto dto = new TaskDto(null, "To Delete", "Desc", Task.Priority.MEDIUM, Task.Status.TODO, null);
        Task created = taskService.create(dto);
        UUID id = created.getId();

        // When
        taskService.delete(id);

        // Then
        assertThatThrownBy(() -> taskService.get(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void shouldListAllTasks() {
        // Given
        taskService.create(new TaskDto(null, "Task 1", "Desc", Task.Priority.HIGH, Task.Status.TODO, null));
        taskService.create(new TaskDto(null, "Task 2", "Desc", Task.Priority.MEDIUM, Task.Status.IN_PROGRESS, null));
        taskService.create(new TaskDto(null, "Task 3", "Desc", Task.Priority.LOW, Task.Status.DONE, null));

        // When
        Page<Task> tasks = taskService.list(0, 10, null, null, "createdAt", "DESC");

        // Then
        assertThat(tasks.getContent()).hasSize(3);
        assertThat(tasks.getTotalElements()).isEqualTo(3);
    }

    @Test
    void shouldFilterByStatus() {
        // Given
        taskService.create(new TaskDto(null, "TODO Task", "Desc", Task.Priority.HIGH, Task.Status.TODO, null));
        taskService.create(new TaskDto(null, "DONE Task", "Desc", Task.Priority.MEDIUM, Task.Status.DONE, null));

        // When
        Page<Task> todoTasks = taskService.list(0, 10, Task.Status.TODO, null, "createdAt", "DESC");

        // Then
        assertThat(todoTasks.getContent()).hasSize(1);
        assertThat(todoTasks.getContent().get(0).getStatus()).isEqualTo(Task.Status.TODO);
    }

    @Test
    void shouldFilterByPriority() {
        // Given
        taskService.create(new TaskDto(null, "High Priority", "Desc", Task.Priority.HIGH, Task.Status.TODO, null));
        taskService.create(new TaskDto(null, "Low Priority", "Desc", Task.Priority.LOW, Task.Status.TODO, null));

        // When
        Page<Task> highPriorityTasks = taskService.list(0, 10, null, Task.Priority.HIGH, "createdAt", "DESC");

        // Then
        assertThat(highPriorityTasks.getContent()).hasSize(1);
        assertThat(highPriorityTasks.getContent().get(0).getPriority()).isEqualTo(Task.Priority.HIGH);
    }

    @Test
    void shouldHandlePagination() {
        // Given
        for (int i = 1; i <= 15; i++) {
            taskService.create(new TaskDto(null, "Task " + i, "Desc", Task.Priority.MEDIUM, Task.Status.TODO, null));
        }

        // When
        Page<Task> page1 = taskService.list(0, 10, null, null, "createdAt", "DESC");
        Page<Task> page2 = taskService.list(1, 10, null, null, "createdAt", "DESC");

        // Then
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page2.getContent()).hasSize(5);
        assertThat(page1.getTotalPages()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(15);
    }
}

