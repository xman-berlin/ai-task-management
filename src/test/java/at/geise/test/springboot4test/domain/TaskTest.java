package at.geise.test.springboot4test.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void shouldCreateTaskWithDefaultValues() {
        // When
        Task task = new Task();

        // Then
        assertThat(task.getPriority()).isEqualTo(Task.Priority.MEDIUM);
        assertThat(task.getStatus()).isEqualTo(Task.Status.TODO);
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Given
        Task task = new Task();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(5);

        // When
        task.setId(id);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(Task.Priority.HIGH);
        task.setStatus(Task.Status.IN_PROGRESS);
        task.setDueDate(futureDate);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        // Then
        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Test Description");
        assertThat(task.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(task.getStatus()).isEqualTo(Task.Status.IN_PROGRESS);
        assertThat(task.getDueDate()).isEqualTo(futureDate);
        assertThat(task.getCreatedAt()).isEqualTo(now);
        assertThat(task.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void priorityEnum_shouldHaveAllValues() {
        // When/Then
        assertThat(Task.Priority.values()).containsExactly(
                Task.Priority.LOW,
                Task.Priority.MEDIUM,
                Task.Priority.HIGH
        );
    }

    @Test
    void statusEnum_shouldHaveAllValues() {
        // When/Then
        assertThat(Task.Status.values()).containsExactly(
                Task.Status.TODO,
                Task.Status.IN_PROGRESS,
                Task.Status.DONE
        );
    }

    @Test
    void priorityEnum_shouldBeConvertibleFromString() {
        // When/Then
        assertThat(Task.Priority.valueOf("LOW")).isEqualTo(Task.Priority.LOW);
        assertThat(Task.Priority.valueOf("MEDIUM")).isEqualTo(Task.Priority.MEDIUM);
        assertThat(Task.Priority.valueOf("HIGH")).isEqualTo(Task.Priority.HIGH);
    }

    @Test
    void statusEnum_shouldBeConvertibleFromString() {
        // When/Then
        assertThat(Task.Status.valueOf("TODO")).isEqualTo(Task.Status.TODO);
        assertThat(Task.Status.valueOf("IN_PROGRESS")).isEqualTo(Task.Status.IN_PROGRESS);
        assertThat(Task.Status.valueOf("DONE")).isEqualTo(Task.Status.DONE);
    }
}

