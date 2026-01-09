package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.AiTaskSuggestionRequest;
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

    public PrioritySuggestion prioritize(AiTaskSuggestionRequest request) {
        try {
            String prompt = String.format(
                PRIORITIZE_PROMPT,
                request.title() != null ? request.title() : "No title",
                request.description() != null ? request.description() : "No description",
                request.dueDate() != null ? request.dueDate().toString() : "Not set",
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
            return fallbackPrioritize(request);
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


    private PrioritySuggestion fallbackPrioritize(AiTaskSuggestionRequest request) {
        Task.Priority priority = Task.Priority.MEDIUM;
        String rationale = "Defaulted to MEDIUM (AI unavailable)";
        if (request.dueDate() != null && request.dueDate().isBefore(LocalDateTime.now().plus(2, ChronoUnit.DAYS))) {
            priority = Task.Priority.HIGH;
            rationale = "Due in <48h";
        } else if (request.description() != null && request.description().length() > 500) {
            priority = Task.Priority.LOW;
            rationale = "Long description suggests research";
        }
        return new PrioritySuggestion(priority, rationale);
    }

    public DecompositionSuggestion decompose(AiTaskSuggestionRequest request) {
        try {
            String prompt = String.format("""
                You are an expert project manager helping break down tasks into manageable subtasks.
                
                Analyze this task and suggest 3-5 actionable subtasks to complete it.
                
                Task Details:
                - Title: %s
                - Description: %s
                - Due Date: %s
                
                Consider:
                1. Logical sequence of steps
                2. Dependencies between subtasks
                3. Clarity and actionability
                4. Reasonable scope for each subtask
                
                Respond ONLY with a JSON object in this exact format:
                {
                  "subtasks": [
                    "First actionable subtask",
                    "Second actionable subtask",
                    "Third actionable subtask"
                  ]
                }
                """,
                request.title() != null ? request.title() : "No title",
                request.description() != null ? request.description() : "No description",
                request.dueDate() != null ? request.dueDate().toString() : "Not set"
            );

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", "You are a helpful project management assistant."),
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

            log.info("AI decomposition response: {}", response);

            return parseDecompositionResponse(response);

        } catch (Exception e) {
            log.warn("AI decomposition failed, falling back to generic subtasks: {}", e.getMessage());
            return fallbackDecompose(request);
        }
    }

    private DecompositionSuggestion parseDecompositionResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

            log.debug("Extracted decomposition content: {}", content);

            String cleaned = content.trim()
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

            JsonNode result = objectMapper.readTree(cleaned);
            List<String> subtasks = new java.util.ArrayList<>();
            result.path("subtasks").forEach(node -> subtasks.add(node.asText()));

            return new DecompositionSuggestion(subtasks);

        } catch (Exception e) {
            log.warn("Failed to parse decomposition response: {}", e.getMessage());
            return new DecompositionSuggestion(List.of("Break down this task", "Define acceptance criteria", "Complete implementation"));
        }
    }

    private DecompositionSuggestion fallbackDecompose(AiTaskSuggestionRequest request) {
        List<String> subtasks = List.of(
            "Clarify requirements for: " + (request.title() != null ? request.title() : "this task"),
            "Identify owners and resources",
            "Define acceptance criteria",
            "Create implementation plan",
            "Review and test"
        );
        return new DecompositionSuggestion(subtasks);
    }

    public DeadlineSuggestion predictDeadline(AiTaskSuggestionRequest request) {
        try {
            String prompt = String.format("""
                You are an expert project manager helping estimate realistic deadlines.
                
                Analyze this task and suggest a realistic deadline based on its complexity and scope.
                
                Task Details:
                - Title: %s
                - Description: %s
                - Current Date: %s
                
                Consider:
                1. Task complexity and scope
                2. Typical time needed for similar tasks
                3. Buffer for unexpected issues
                4. Realistic working pace
                
                Respond ONLY with a JSON object in this exact format:
                {
                  "days": 7,
                  "rationale": "Brief explanation of why this timeline is realistic"
                }
                
                Where 'days' is a number between 1 and 90 representing days from now.
                """,
                request.title() != null ? request.title() : "No title",
                request.description() != null ? request.description() : "No description",
                LocalDateTime.now().toString()
            );

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", "You are a helpful project management assistant."),
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

            log.info("AI deadline prediction response: {}", response);

            return parseDeadlineResponse(response);

        } catch (Exception e) {
            log.warn("AI deadline prediction failed, falling back to heuristic: {}", e.getMessage());
            return fallbackDeadline(request);
        }
    }

    private DeadlineSuggestion parseDeadlineResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

            log.debug("Extracted deadline content: {}", content);

            String cleaned = content.trim()
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

            JsonNode result = objectMapper.readTree(cleaned);
            int days = result.path("days").asInt(7);
            String rationale = result.path("rationale").asText("AI suggested deadline");

            LocalDateTime suggestedDeadline = LocalDateTime.now().plusDays(days);
            return new DeadlineSuggestion(suggestedDeadline, rationale);

        } catch (Exception e) {
            log.warn("Failed to parse deadline response: {}", e.getMessage());
            LocalDateTime suggested = LocalDateTime.now().plusDays(7);
            return new DeadlineSuggestion(suggested, "Default 7-day timeline");
        }
    }

    private DeadlineSuggestion fallbackDeadline(AiTaskSuggestionRequest request) {
        int days = 7;
        String rationale = "Standard 1-week timeline (AI unavailable)";

        if (request.description() != null && request.description().length() > 500) {
            days = 14;
            rationale = "Complex task, 2-week timeline recommended";
        } else if (request.title() != null && request.title().toLowerCase().contains("urgent")) {
            days = 3;
            rationale = "Urgent task, 3-day timeline";
        }

        LocalDateTime suggested = LocalDateTime.now().plusDays(days);
        return new DeadlineSuggestion(suggested, rationale);
    }

    public record PrioritySuggestion(Task.Priority priority, String rationale) {}
    public record DecompositionSuggestion(List<String> subtasks) {}
    public record DeadlineSuggestion(LocalDateTime suggestedDueDate, String rationale) {}
}
