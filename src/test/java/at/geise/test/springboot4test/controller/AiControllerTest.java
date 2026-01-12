package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.AiTaskSuggestionRequest;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDto testDto;

    @BeforeEach
    void setUp() {

        testDto = new TaskDto(
                null,
                "Implement user authentication",
                "Add OAuth2 login functionality",
                Task.Priority.HIGH,
                Task.Status.TODO,
                LocalDateTime.now().plusDays(7)
        );
    }

    @Test
    void prioritize_shouldReturnPrioritySuggestion() throws Exception {
        // Given
        AiService.PrioritySuggestion suggestion = new AiService.PrioritySuggestion(
                Task.Priority.HIGH,
                "This task is critical for security and should be prioritized"
        );
        when(aiService.prioritize(any(AiTaskSuggestionRequest.class))).thenReturn(suggestion);

        AiTaskSuggestionRequest request = new AiTaskSuggestionRequest(
                testDto.title(),
                testDto.description(),
                testDto.dueDate()
        );

        // When/Then
        mockMvc.perform(post("/api/ai/prioritize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.rationale").value("This task is critical for security and should be prioritized"));

        verify(aiService).prioritize(any(AiTaskSuggestionRequest.class));
    }

    @Test
    void decompose_shouldReturnSubtaskSuggestions() throws Exception {
        // Given
        AiService.DecompositionSuggestion suggestion = new AiService.DecompositionSuggestion(
                List.of(
                        "Set up OAuth2 provider configuration",
                        "Implement login endpoints",
                        "Add user session management",
                        "Write integration tests"
                )
        );
        when(aiService.decompose(any(AiTaskSuggestionRequest.class))).thenReturn(suggestion);

        AiTaskSuggestionRequest request = new AiTaskSuggestionRequest(
                testDto.title(),
                testDto.description(),
                testDto.dueDate()
        );

        // When/Then
        mockMvc.perform(post("/api/ai/decompose")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtasks").isArray())
                .andExpect(jsonPath("$.subtasks[0]").value("Set up OAuth2 provider configuration"))
                .andExpect(jsonPath("$.subtasks[1]").value("Implement login endpoints"));

        verify(aiService).decompose(any(AiTaskSuggestionRequest.class));
    }

    @Test
    void deadline_shouldReturnDeadlineSuggestion() throws Exception {
        // Given
        LocalDateTime suggestedDeadline = LocalDateTime.now().plusDays(10);
        AiService.DeadlineSuggestion suggestion = new AiService.DeadlineSuggestion(
                suggestedDeadline,
                "Based on complexity, this task requires approximately 10 days"
        );
        when(aiService.predictDeadline(any(AiTaskSuggestionRequest.class))).thenReturn(suggestion);

        AiTaskSuggestionRequest request = new AiTaskSuggestionRequest(
                testDto.title(),
                testDto.description(),
                testDto.dueDate()
        );

        // When/Then
        mockMvc.perform(post("/api/ai/deadline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedDeadline").exists())
                .andExpect(jsonPath("$.rationale").value("Based on complexity, this task requires approximately 10 days"));

        verify(aiService).predictDeadline(any(AiTaskSuggestionRequest.class));
    }

    @Test
    void prioritize_shouldValidateRequest() throws Exception {
        // Given - invalid request with null title
        AiTaskSuggestionRequest invalidRequest = new AiTaskSuggestionRequest(
                null,
                null,
                null
        );

        // When/Then
        mockMvc.perform(post("/api/ai/prioritize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

