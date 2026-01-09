package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.dto.AiTaskSuggestionRequest;
import at.geise.test.springboot4test.service.AiService;
import at.geise.test.springboot4test.service.AiService.DeadlineSuggestion;
import at.geise.test.springboot4test.service.AiService.DecompositionSuggestion;
import at.geise.test.springboot4test.service.AiService.PrioritySuggestion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/prioritize")
    public PrioritySuggestion prioritize(@RequestBody @Valid AiTaskSuggestionRequest request) {
        return aiService.prioritize(request);
    }

    @PostMapping("/decompose")
    public DecompositionSuggestion decompose(@RequestBody @Valid AiTaskSuggestionRequest request) {
        return aiService.decompose(request);
    }

    @PostMapping("/deadline")
    public DeadlineSuggestion deadline(@RequestBody @Valid AiTaskSuggestionRequest request) {
        return aiService.predictDeadline(request);
    }
}

