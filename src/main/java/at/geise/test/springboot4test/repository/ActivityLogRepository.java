package at.geise.test.springboot4test.repository;

import at.geise.test.springboot4test.domain.ActivityLog;
import at.geise.test.springboot4test.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    List<ActivityLog> findByTaskOrderByTimestampDesc(Task task);

    Page<ActivityLog> findByTaskOrderByTimestampDesc(Task task, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.task.id = :taskId ORDER BY a.timestamp DESC")
    List<ActivityLog> findByTaskIdOrderByTimestampDesc(@Param("taskId") UUID taskId);

    @Query("SELECT a FROM ActivityLog a WHERE a.task.id = :taskId ORDER BY a.timestamp DESC")
    Page<ActivityLog> findByTaskIdOrderByTimestampDesc(@Param("taskId") UUID taskId, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.task.id IN :taskIds ORDER BY a.timestamp DESC")
    List<ActivityLog> findByTaskIdsOrderByTimestampDesc(@Param("taskIds") List<UUID> taskIds);

    long countByTask(Task task);
}

