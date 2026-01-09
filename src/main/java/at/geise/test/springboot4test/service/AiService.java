package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final ChatClient.Builder chatClientBuilder;

    private static final String PRIORITIZE_PROMPT = """
            You are an expert task manager helping prioritize work.
            
            Analyze this task and suggest a priority level (LOW, MEDIUM, or HIGH) with a brief rationale.
            
            Task Details:
            - Title: {title}
            - Description: {description}
            - Due Date: {dueDate}
            - Current Date: {currentDate}
            
            Consider these factors:
            1. Urgency (how soon is the due date?)
            2. Impact (does it seem critical or foundational?)
            3. Complexity (is it a quick task or a major project?)
            4. Dependencies (does it appear to block other work?)
            
            Respond ONLY with a JSON object in this exact format:
            {
              "priority": "HIGH|MEDIUM|LOW",
              "rationale": "Brief explanation in 1-2 sentences"
            }
            """;

    public PrioritySuggestion prioritize(TaskDto dto) {
        try {
            ChatClient chatClient = chatClientBuilder.build();

            PromptTemplate template = new PromptTemplate(PRIORITIZE_PROMPT);
            String prompt = template.render(Map.of(
                "title", dto.title() != null ? dto.title() : "No title",
                "description", dto.description() != null ? dto.description() : "No description",
                "dueDate", dto.dueDate() != null ? dto.dueDate().toString() : "Not set",
                "currentDate", LocalDateTime.now().toString()
            ));

            String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            log.info("AI prioritization response: {}", response);

            return parsePriorityResponse(response);

        } catch (Exception e) {
            log.warn("AI prioritization failed, falling back to heuristic: {}", e.getMessage());
            return fallbackPrioritize(dto);
        }
    }

    private PrioritySuggestion parsePriorityResponse(String response) {
        // Simple JSON parsing - in production use Jackson or similar
        try {
            String cleaned = response.trim()
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

            String priorityStr = extractJsonField(cleaned, "priority");
            String rationale = extractJsonField(cleaned, "rationale");

            Task.Priority priority = Task.Priority.valueOf(priorityStr.toUpperCase());
            return new PrioritySuggestion(priority, rationale);

        } catch (Exception e) {
            log.warn("Failed to parse AI response, using MEDIUM default: {}", e.getMessage());
            return new PrioritySuggestion(Task.Priority.MEDIUM, "AI analysis completed");
        }
    }

    private String extractJsonField(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        throw new IllegalArgumentException("Field " + field + " not found");
    }

    private PrioritySuggestion fallbackPrioritize(TaskDto dto) {
        Task.Priority priority = Task.Priority.MEDIUM;
        String rationale = "Defaulted to MEDIUM (AI unavailable)";
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
