package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.ActivityLog;
import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.ActivityLogDto;
import at.geise.test.springboot4test.dto.ActivitySummaryDto;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.repository.ActivityLogRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final TaskService taskService;

    public ActivityLogService(ActivityLogRepository activityLogRepository, @Lazy TaskService taskService) {
        this.activityLogRepository = activityLogRepository;
        this.taskService = taskService;
    }

    public List<ActivityLogDto> getActivityForTask(UUID taskId) {
        Task task = taskService.get(taskId);
        return activityLogRepository.findByTaskOrderByTimestampDesc(task)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<ActivityLogDto> getActivityForTask(UUID taskId, int page, int size) {
        Task task = taskService.get(taskId);
        Pageable pageable = PageRequest.of(page, size);
        return activityLogRepository.findByTaskOrderByTimestampDesc(task, pageable)
                .map(this::toDto);
    }

    public List<ActivityLogDto> getRecentActivity(int limit) {
        // Get recent activity across all tasks
        Pageable pageable = PageRequest.of(0, limit);
        return activityLogRepository.findAll(pageable)
                .map(this::toDto)
                .getContent();
    }

    // Logging methods
    public void logTaskCreated(Task task, String author) {
        ActivityLog activityLog = ActivityLog.taskCreated(task, author);
        activityLogRepository.save(activityLog);
    }

    public void logStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus, String author) {
        ActivityLog activityLog = ActivityLog.statusChanged(task, oldStatus, newStatus, author);
        activityLogRepository.save(activityLog);
    }

    public void logPriorityChanged(Task task, Task.Priority oldPriority, Task.Priority newPriority, String author) {
        ActivityLog activityLog = ActivityLog.priorityChanged(task, oldPriority, newPriority, author);
        activityLogRepository.save(activityLog);
    }

    public void logTitleChanged(Task task, String oldTitle, String newTitle, String author) {
        ActivityLog activityLog = ActivityLog.titleChanged(task, oldTitle, newTitle, author);
        activityLogRepository.save(activityLog);
    }

    public void logDescriptionChanged(Task task, String oldDescription, String newDescription, String author) {
        ActivityLog activityLog = ActivityLog.descriptionChanged(task, oldDescription, newDescription, author);
        activityLogRepository.save(activityLog);
    }

    public void logDueDateChanged(Task task, String oldDueDate, String newDueDate, String author) {
        ActivityLog activityLog = ActivityLog.dueDateChanged(task, oldDueDate, newDueDate, author);
        activityLogRepository.save(activityLog);
    }

    public void logCommentAdded(Task task, String author) {
        ActivityLog activityLog = ActivityLog.commentAdded(task, author);
        activityLogRepository.save(activityLog);
    }

    public void logTaskUpdated(Task task, Task.Status oldStatus, Task.Priority oldPriority, String oldTitle, String oldDescription, LocalDateTime oldDueDate, TaskDto newDto) {
        // Log individual field changes
        if (!oldStatus.equals(newDto.status())) {
            logStatusChanged(task, oldStatus, newDto.status(), "System");
        }
        if (!oldPriority.equals(newDto.priority())) {
            logPriorityChanged(task, oldPriority, newDto.priority(), "System");
        }
        if (!oldTitle.equals(newDto.title())) {
            logTitleChanged(task, oldTitle, newDto.title(), "System");
        }
        if (!java.util.Objects.equals(oldDescription, newDto.description())) {
            logDescriptionChanged(task, oldDescription, newDto.description(), "System");
        }
        if (!java.util.Objects.equals(oldDueDate, newDto.dueDate())) {
            logDueDateChanged(task,
                oldDueDate != null ? oldDueDate.toString() : null,
                newDto.dueDate() != null ? newDto.dueDate().toString() : null,
                "System");
        }
    }

    public long getActivityCountForTask(UUID taskId) {
        Task task = taskService.get(taskId);
        return activityLogRepository.countByTask(task);
    }

    public List<ActivitySummaryDto> getActivitySummary(UUID taskId) {
        // This could be implemented with custom queries to get aggregated activity data
        // For now, return a simple summary
        Task task = taskService.get(taskId);
        List<ActivityLog> activities = activityLogRepository.findByTaskOrderByTimestampDesc(task);

        return activities.stream()
                .collect(Collectors.groupingBy(ActivityLog::getAction))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<ActivityLog> logs = entry.getValue();
                    LocalDateTime lastActivity = logs.stream()
                            .map(ActivityLog::getTimestamp)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);
                    return new ActivitySummaryDto(entry.getKey(), logs.size(), lastActivity);
                })
                .collect(Collectors.toList());
    }

    private ActivityLogDto toDto(ActivityLog activityLog) {
        return new ActivityLogDto(
                activityLog.getId(),
                activityLog.getTask().getId(),
                activityLog.getAction(),
                activityLog.getOldValue(),
                activityLog.getNewValue(),
                activityLog.getAuthor(),
                activityLog.getTimestamp()
        );
    }
}
