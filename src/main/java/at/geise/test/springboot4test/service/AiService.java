package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final WebClient aiWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.api.model}")
    private String model;

    @Value("${ai.api.temperature}")
    private double temperature;

    @Value("${ai.api.max-tokens}")
    private int maxTokens;

    private static final String PRIORITIZE_PROMPT = """
            You are an expert task manager helping prioritize work.
            
            Analyze this task and suggest a priority level (LOW, MEDIUM, or HIGH) with a brief rationale.
            
            Task Details:
            - Title: %s
            - Description: %s
            - Due Date: %s
            - Current Date: %s
            
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
            String prompt = String.format(
                PRIORITIZE_PROMPT,
                dto.title() != null ? dto.title() : "No title",
                dto.description() != null ? dto.description() : "No description",
                dto.dueDate() != null ? dto.dueDate().toString() : "Not set",
                LocalDateTime.now().toString()
            );

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", "You are a helpful task management assistant."),
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", temperature,
                "max_tokens", maxTokens
            );

            String response = aiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("AI API response: {}", response);

            return parseApiResponse(response);

        } catch (Exception e) {
            log.warn("AI prioritization failed, falling back to heuristic: {}", e.getMessage());
            return fallbackPrioritize(dto);
        }
    }

    private PrioritySuggestion parseApiResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

            log.debug("Extracted AI content: {}", content);

            // Clean markdown code blocks if present
            String cleaned = content.trim()
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

            JsonNode result = objectMapper.readTree(cleaned);
            String priorityStr = result.path("priority").asText();
            String rationale = result.path("rationale").asText();

            Task.Priority priority = Task.Priority.valueOf(priorityStr.toUpperCase());
            return new PrioritySuggestion(priority, rationale);

        } catch (Exception e) {
            log.warn("Failed to parse AI response, using MEDIUM default: {}", e.getMessage());
            return new PrioritySuggestion(Task.Priority.MEDIUM, "AI analysis completed");
        }
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
