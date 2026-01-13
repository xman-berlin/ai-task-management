package at.geise.test.springboot4test.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    Task task;

    @NotBlank
    @Size(max = 50)
    String action; // e.g., "CREATED", "UPDATED_STATUS", "UPDATED_PRIORITY", "COMMENTED"

    @Size(max = 255)
    String oldValue; // e.g., "TODO", "LOW"

    @Size(max = 255)
    String newValue; // e.g., "IN_PROGRESS", "HIGH"

    @NotBlank
    @Size(max = 100)
    String author = "System"; // For now, we'll use a default author

    LocalDateTime timestamp = LocalDateTime.now();

    public ActivityLog(Task task, String action, String oldValue, String newValue, String author) {
        this.task = task;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.author = author != null ? author : "System";
    }

    // Helper methods for common actions
    public static ActivityLog taskCreated(Task task, String author) {
        return new ActivityLog(task, "CREATED", null, null, author);
    }

    public static ActivityLog statusChanged(Task task, Task.Status oldStatus, Task.Status newStatus, String author) {
        return new ActivityLog(task, "STATUS_CHANGED",
                oldStatus != null ? oldStatus.name() : null,
                newStatus.name(), author);
    }

    public static ActivityLog priorityChanged(Task task, Task.Priority oldPriority, Task.Priority newPriority, String author) {
        return new ActivityLog(task, "PRIORITY_CHANGED",
                oldPriority != null ? oldPriority.name() : null,
                newPriority.name(), author);
    }

    public static ActivityLog titleChanged(Task task, String oldTitle, String newTitle, String author) {
        return new ActivityLog(task, "TITLE_CHANGED", oldTitle, newTitle, author);
    }

    public static ActivityLog descriptionChanged(Task task, String oldDescription, String newDescription, String author) {
        return new ActivityLog(task, "DESCRIPTION_CHANGED", oldDescription, newDescription, author);
    }

    public static ActivityLog dueDateChanged(Task task, String oldDueDate, String newDueDate, String author) {
        return new ActivityLog(task, "DUE_DATE_CHANGED", oldDueDate, newDueDate, author);
    }

    public static ActivityLog commentAdded(Task task, String author) {
        return new ActivityLog(task, "COMMENT_ADDED", null, null, author);
    }
}

