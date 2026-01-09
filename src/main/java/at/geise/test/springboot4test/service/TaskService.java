package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public Page<Task> list(Integer page, Integer size, Task.Status status, Task.Priority priority, String sortBy, String direction) {
        PageRequest pr = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 10 : size,
                Sort.Direction.fromString(direction != null ? direction : "DESC"),
                sortBy != null ? sortBy : "createdAt"
        );
        if (status != null) {
            return repository.findByStatus(status, pr);
        }
        if (priority != null) {
            return repository.findByPriority(priority, pr);
        }
        return repository.findAll(pr);
    }

    public Task get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public Task create(TaskDto dto) {
        Task task = new Task();
        apply(dto, task);
        task.setCreatedAt(LocalDateTime.now());
        return repository.save(task);
    }

    public Task update(UUID id, TaskDto dto) {
        Task task = get(id);
        apply(dto, task);
        task.setUpdatedAt(LocalDateTime.now());
        return repository.save(task);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private static void apply(TaskDto dto, Task task) {
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(dto.priority());
        task.setStatus(dto.status());
        task.setDueDate(dto.dueDate());
    }
}
