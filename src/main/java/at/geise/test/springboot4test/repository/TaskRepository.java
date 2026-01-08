package at.geise.test.springboot4test.repository;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.domain.Task.Status;
import at.geise.test.springboot4test.domain.Task.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByPriority(Priority priority, Pageable pageable);
}

