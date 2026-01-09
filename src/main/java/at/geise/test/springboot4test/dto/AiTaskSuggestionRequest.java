package at.geise.test.springboot4test.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for AI suggestion requests - no required priority/status since we're suggesting those
 */
public record AiTaskSuggestionRequest(
        @Size(max = 255) String title,
        @Size(max = 4000) String description,
        LocalDateTime dueDate
) {}

