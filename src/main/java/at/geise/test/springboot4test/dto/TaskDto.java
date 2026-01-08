package at.geise.test.springboot4test.dto;

import at.geise.test.springboot4test.domain.Task.Priority;
import at.geise.test.springboot4test.domain.Task.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 4000) String description,
        @NotNull Priority priority,
        @NotNull Status status,
        @FutureOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate
) {}
