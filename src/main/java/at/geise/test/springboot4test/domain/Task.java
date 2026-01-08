package at.geise.test.springboot4test.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
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
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotBlank
    @Size(max = 255)
    String title;

    @Size(max = 4000)
    String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    Priority priority = Priority.MEDIUM;

    @NotNull
    @Enumerated(EnumType.STRING)
    Status status = Status.TODO;

    @FutureOrPresent
    LocalDateTime dueDate;

    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt;

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { TODO, IN_PROGRESS, DONE }
}
