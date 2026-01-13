package at.geise.test.springboot4test.config;

import at.geise.test.springboot4test.domain.Comment;
import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.domain.Task.Priority;
import at.geise.test.springboot4test.domain.Task.Status;
import at.geise.test.springboot4test.repository.CommentRepository;
import at.geise.test.springboot4test.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class StartupDataLoader implements ApplicationRunner {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (taskRepository.count() > 0) {
            return; // keep existing data
        }

        // Disable activity logging during startup data load
        TaskChangeListener.setSkipLogging(true);
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Task> seeds = List.of(
                    // ...existing code...
                build("AI roadmap draft", "Outline vision, milestones, and KPIs for first 12 months.", Priority.HIGH, Status.TODO, now.plusDays(7)),
                build("MVP architecture", "Define service boundaries, data model, and security baselines.", Priority.HIGH, Status.IN_PROGRESS, now.plusDays(5)),
                build("Model selection", "Compare GPT-4-turbo vs. local LLM for core agent tasks.", Priority.MEDIUM, Status.TODO, now.plusDays(10)),
                build("Prompt library", "Create reusable prompt templates for prioritization and decomposition.", Priority.MEDIUM, Status.TODO, now.plusDays(8)),
                build("Eval harness", "Set up evals with golden tasks and acceptance criteria for agents.", Priority.HIGH, Status.TODO, now.plusDays(12)),
                build("Data privacy review", "Document PII handling, retention, and redaction strategy.", Priority.HIGH, Status.TODO, now.plusDays(6)),
                build("Observability setup", "Add tracing, metrics, and logging for AI calls and CRUD API.", Priority.MEDIUM, Status.TODO, now.plusDays(9)),
                build("UI prototype", "Ship responsive dashboard with task list, detail, and AI panel.", Priority.MEDIUM, Status.IN_PROGRESS, now.plusDays(4)),
                build("Onboarding flow", "Create demo data and first-run walkthrough for new users.", Priority.LOW, Status.TODO, now.plusDays(14)),
                build("CI pipeline", "Add CI for tests, formatting, and dependency scanning.", Priority.HIGH, Status.TODO, now.plusDays(3)),
                // Additional suggested seeds (from DataSeeder)
                build("URGENT: Patch production outage in payment service", "Error 500 spikes in checkout flow; identify root cause and deploy hotfix.", Priority.HIGH, Status.IN_PROGRESS, now.plusHours(6)),
                build("Apply critical security update (CVE-2026-1234) to backend", "Upgrade vulnerable dependencies; run regression tests.", Priority.HIGH, Status.TODO, now.plusDays(1)),
                build("Restore failed nightly backups and verify integrity", "Restore from last successful snapshot; verify data consistency.", Priority.HIGH, Status.TODO, now.plusDays(2)),
                build("Unblock deployment by updating Stripe SDK to latest", "SDK upgrade required for 3DS; update and run payment tests.", Priority.MEDIUM, Status.TODO, now.plusDays(3)),
                build("Finalize SSO provider configuration to unblock onboarding", "Configure SAML/OIDC; map roles and test user provisioning.", Priority.MEDIUM, Status.TODO, now.plusDays(5)),
                build("Build recommendation engine MVP (RAG + embeddings)", "Ingest KB, generate embeddings, implement retrieval + reranking.", Priority.MEDIUM, Status.TODO, now.plusDays(14)),
                build("Design and roll out feature flagging framework", "Evaluate Unleash/FF4J; implement per-env flags + SDK usage.", Priority.MEDIUM, Status.TODO, now.plusDays(12)),
                build("Research vector database options (pgvector vs. Qdrant vs. Milvus)", "Compare performance, cost, and operational complexity.", Priority.LOW, Status.TODO, now.plusDays(21)),
                build("Evaluate LLM context window strategies for long docs", "Chunking, RAG, map-reduce; pick best approach.", Priority.LOW, Status.TODO, now.plusDays(21)),
                build("Add password strength meter to signup form", "Use zxcvbn; UX tweaks for guidance.", Priority.LOW, Status.TODO, now.plusDays(7)),
                build("Update landing page copy for pricing clarity", "Clarify tiers and overage policy; A/B test variants.", Priority.LOW, Status.TODO, now.plusDays(4)),
                build("Fine-tune intent classifier on latest support tickets", "Label dataset, train baseline, evaluate and iterate.", Priority.MEDIUM, Status.TODO, now.plusDays(9)),
                build("Implement RAG pipeline for knowledge base search", "ETL docs, embed, index, retrieval, response synthesis.", Priority.MEDIUM, Status.TODO, now.plusDays(15)),
                build("Rotate all production credentials and audit access", "Rotate keys/tokens, enforce least privilege, document changes.", Priority.HIGH, Status.TODO, now.plusDays(3))
            );

            taskRepository.saveAll(seeds);

            // Add sample comments to each task
            for (Task task : seeds) {
                addSampleComments(task);
            }
        } finally {
            // Re-enable activity logging after startup data load
            TaskChangeListener.setSkipLogging(false);
        }
    }

    private void addSampleComments(Task task) {
        String[] comments = {
            "Started working on this task. Looking into the requirements.",
            "Made good progress today. Almost halfway done.",
            "Found a few blockers, discussing with team to resolve.",
            "Great feedback from review. Implementing suggestions now.",
            "Task completed and tested. Ready for merge."
        };

        for (int i = 0; i < comments.length; i++) {
            Comment comment = new Comment(
                task,
                comments[i],
                i % 2 == 0 ? "Alice" : "Bob"
            );
            comment.setCreatedAt(task.getCreatedAt().plusHours(i + 1));
            commentRepository.save(comment);
        }
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
