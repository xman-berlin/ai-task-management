package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AiService {

    public PrioritySuggestion prioritize(TaskDto dto) {
        Task.Priority priority = Task.Priority.MEDIUM;
        String rationale = "Defaulted to MEDIUM";
        if (dto.dueDate() != null && dto.dueDate().isBefore(LocalDateTime.now().plus(2, ChronoUnit.DAYS))) {
            priority = Task.Priority.HIGH;
            rationale = "Due in <48h";
        } else if (dto.description() != null && dto.description().length() > 500) {
            priority = Task.Priority.LOW;
            rationale = "Long description suggests research";
        }
        return new PrioritySuggestion(priority, rationale);
    }

    public DecompositionSuggestion decompose(TaskDto dto) {
        // stub: split by sentences if available
        List<String> subtasks = List.of(
                "Clarify requirements for: " + dto.title(),
                "Identify owners and resources",
                "Define acceptance criteria"
        );
        return new DecompositionSuggestion(subtasks);
    }

    public DeadlineSuggestion predictDeadline(TaskDto dto) {
        LocalDateTime suggested = LocalDateTime.now().plusDays(7);
        return new DeadlineSuggestion(suggested, "Stubbed +7 days suggestion");
    }

    public record PrioritySuggestion(Task.Priority priority, String rationale) {}
    public record DecompositionSuggestion(List<String> subtasks) {}
    public record DeadlineSuggestion(LocalDateTime suggestedDueDate, String rationale) {}
}
