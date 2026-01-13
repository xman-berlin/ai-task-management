package at.geise.test.springboot4test.repository;

import at.geise.test.springboot4test.domain.Comment;
import at.geise.test.springboot4test.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByTaskOrderByCreatedAtAsc(Task task);

    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId ORDER BY c.createdAt ASC")
    List<Comment> findByTaskIdOrderByCreatedAtAsc(@Param("taskId") UUID taskId);

    long countByTask(Task task);
}

