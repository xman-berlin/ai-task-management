package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task testTask;
    private TaskDto testDto;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testTask = new Task();
        testTask.setId(testId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setPriority(Task.Priority.HIGH);
        testTask.setStatus(Task.Status.TODO);
        testTask.setDueDate(LocalDateTime.now().plusDays(1));
        testTask.setCreatedAt(LocalDateTime.now());

        testDto = new TaskDto(
                testId,
                "Test Task",
                "Test Description",
                Task.Priority.HIGH,
                Task.Status.TODO,
                LocalDateTime.now().plusDays(1)
        );
    }

    @Test
    void list_shouldReturnPageOfAllTasks_whenNoFilters() {
        // Given
        Page<Task> expectedPage = new PageImpl<>(List.of(testTask));
        when(repository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Task> result = service.list(null, null, null, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Task");
        verify(repository).findAll(any(PageRequest.class));
    }

    @Test
    void list_shouldReturnPageFilteredByStatus_whenStatusProvided() {
        // Given
        Page<Task> expectedPage = new PageImpl<>(List.of(testTask));
        when(repository.findByStatus(eq(Task.Status.TODO), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Task> result = service.list(null, null, Task.Status.TODO, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findByStatus(eq(Task.Status.TODO), any(PageRequest.class));
    }

    @Test
    void list_shouldReturnPageFilteredByPriority_whenPriorityProvided() {
        // Given
        Page<Task> expectedPage = new PageImpl<>(List.of(testTask));
        when(repository.findByPriority(eq(Task.Priority.HIGH), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Task> result = service.list(null, null, null, Task.Priority.HIGH, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findByPriority(eq(Task.Priority.HIGH), any(PageRequest.class));
    }

    @Test
    void list_shouldUseCustomPageParameters_whenProvided() {
        // Given
        Page<Task> expectedPage = new PageImpl<>(List.of(testTask));
        when(repository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Task> result = service.list(2, 20, null, null, "title", "ASC");

        // Then
        assertThat(result).isNotNull();
        verify(repository).findAll(any(PageRequest.class));
    }

    @Test
    void get_shouldReturnTask_whenTaskExists() {
        // Given
        when(repository.findById(testId)).thenReturn(Optional.of(testTask));

        // When
        Task result = service.get(testId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(repository).findById(testId);
    }

    @Test
    void get_shouldThrowException_whenTaskNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> service.get(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
        verify(repository).findById(nonExistentId);
    }

    @Test
    void create_shouldSaveAndReturnNewTask() {
        // Given
        when(repository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(testId);
            return task;
        });

        // When
        Task result = service.create(testDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(result.getStatus()).isEqualTo(Task.Status.TODO);
        assertThat(result.getCreatedAt()).isNotNull();
        verify(repository).save(any(Task.class));
    }

    @Test
    void update_shouldUpdateAndReturnTask_whenTaskExists() {
        // Given
        when(repository.findById(testId)).thenReturn(Optional.of(testTask));
        when(repository.save(any(Task.class))).thenReturn(testTask);

        TaskDto updateDto = new TaskDto(
                testId,
                "Updated Title",
                "Updated Description",
                Task.Priority.LOW,
                Task.Status.DONE,
                LocalDateTime.now().plusDays(2)
        );

        // When
        Task result = service.update(testId, updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.LOW);
        assertThat(result.getStatus()).isEqualTo(Task.Status.DONE);
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(repository).findById(testId);
        verify(repository).save(testTask);
    }

    @Test
    void update_shouldThrowException_whenTaskNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> service.update(nonExistentId, testDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
        verify(repository).findById(nonExistentId);
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    void delete_shouldDeleteTask() {
        // Given
        doNothing().when(repository).deleteById(testId);

        // When
        service.delete(testId);

        // Then
        verify(repository).deleteById(testId);
    }
}

