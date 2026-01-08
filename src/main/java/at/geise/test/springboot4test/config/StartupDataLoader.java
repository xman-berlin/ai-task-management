package at.geise.test.springboot4test.config;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.domain.Task.Priority;
import at.geise.test.springboot4test.domain.Task.Status;
import at.geise.test.springboot4test.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupDataLoader implements ApplicationRunner {

    private final TaskRepository taskRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (taskRepository.count() > 0) {
            return; // keep existing data
        }

        LocalDateTime now = LocalDateTime.now();
        List<Task> seeds = List.of(
                build("AI roadmap draft", "Outline vision, milestones, and KPIs for first 12 months.", Priority.HIGH, Status.TODO, now.plusDays(7)),
                build("MVP architecture", "Define service boundaries, data model, and security baselines.", Priority.HIGH, Status.IN_PROGRESS, now.plusDays(5)),
                build("Model selection", "Compare GPT-4-turbo vs. local LLM for core agent tasks.", Priority.MEDIUM, Status.TODO, now.plusDays(10)),
                build("Prompt library", "Create reusable prompt templates for prioritization and decomposition.", Priority.MEDIUM, Status.TODO, now.plusDays(8)),
                build("Eval harness", "Set up evals with golden tasks and acceptance criteria for agents.", Priority.HIGH, Status.TODO, now.plusDays(12)),
                build("Data privacy review", "Document PII handling, retention, and redaction strategy.", Priority.HIGH, Status.TODO, now.plusDays(6)),
                build("Observability setup", "Add tracing, metrics, and logging for AI calls and CRUD API.", Priority.MEDIUM, Status.TODO, now.plusDays(9)),
                build("UI prototype", "Ship responsive dashboard with task list, detail, and AI panel.", Priority.MEDIUM, Status.IN_PROGRESS, now.plusDays(4)),
                build("Onboarding flow", "Create demo data and first-run walkthrough for new users.", Priority.LOW, Status.TODO, now.plusDays(14)),
                build("CI pipeline", "Add CI for tests, formatting, and dependency scanning.", Priority.HIGH, Status.TODO, now.plusDays(3))
        );

        taskRepository.saveAll(seeds);
    }

    private static Task build(String title, String description, Priority priority, Status status, LocalDateTime due) {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription(description);
        t.setPriority(priority);
        t.setStatus(status);
        t.setDueDate(due);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }
}

