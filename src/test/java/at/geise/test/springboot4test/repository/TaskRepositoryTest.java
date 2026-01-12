package at.geise.test.springboot4test.repository;

import at.geise.test.springboot4test.domain.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository repository;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setPriority(Task.Priority.HIGH);
        testTask.setStatus(Task.Status.TODO);
        testTask.setDueDate(LocalDateTime.now().plusDays(1));
        testTask.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldSaveAndRetrieveTask() {
        // When
        Task saved = repository.save(testTask);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Task");

        Task found = repository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void findByStatus_shouldReturnTasksWithMatchingStatus() {
        // Given
        repository.save(testTask);

        Task todoTask = new Task();
        todoTask.setTitle("Another TODO");
        todoTask.setDescription("Description");
        todoTask.setPriority(Task.Priority.LOW);
        todoTask.setStatus(Task.Status.TODO);
        repository.save(todoTask);

        Task doneTask = new Task();
        doneTask.setTitle("Done Task");
        doneTask.setDescription("Description");
        doneTask.setPriority(Task.Priority.MEDIUM);
        doneTask.setStatus(Task.Status.DONE);
        repository.save(doneTask);

        entityManager.flush();

        // When
        Page<Task> todoTasks = repository.findByStatus(Task.Status.TODO, PageRequest.of(0, 10));

        // Then
        assertThat(todoTasks.getContent()).hasSize(2);
        assertThat(todoTasks.getContent())
                .allMatch(task -> task.getStatus() == Task.Status.TODO);
    }

    @Test
    void findByPriority_shouldReturnTasksWithMatchingPriority() {
        // Given
        repository.save(testTask);

        Task mediumTask = new Task();
        mediumTask.setTitle("Medium Priority Task");
        mediumTask.setDescription("Description");
        mediumTask.setPriority(Task.Priority.MEDIUM);
        mediumTask.setStatus(Task.Status.TODO);
        repository.save(mediumTask);

        entityManager.flush();

        // When
        Page<Task> highPriorityTasks = repository.findByPriority(Task.Priority.HIGH, PageRequest.of(0, 10));

        // Then
        assertThat(highPriorityTasks.getContent()).hasSize(1);
        assertThat(highPriorityTasks.getContent().get(0).getPriority()).isEqualTo(Task.Priority.HIGH);
    }

    @Test
    void shouldDeleteTask() {
        // Given
        Task saved = repository.save(testTask);
        entityManager.flush();

        // When
        repository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void shouldUpdateTask() {
        // Given
        Task saved = repository.save(testTask);
        entityManager.flush();
        entityManager.clear();

        // When
        saved.setTitle("Updated Title");
        saved.setStatus(Task.Status.DONE);
        Task updated = repository.save(saved);
        entityManager.flush();

        // Then
        Task found = repository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Updated Title");
        assertThat(found.getStatus()).isEqualTo(Task.Status.DONE);
    }
}

