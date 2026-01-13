package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.repository.ActivityLogRepository;
import at.geise.test.springboot4test.repository.CommentRepository;
import at.geise.test.springboot4test.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class UiController {

    private final TaskService service;
    private final ActivityLogRepository activityLogRepository;
    private final CommentRepository commentRepository;

    @GetMapping
    public String index() { return "index"; }

    @GetMapping("/list")
    public String list(@RequestParam(required = false, defaultValue = "0") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer size,
                       @RequestParam(required = false) Task.Status status,
                       @RequestParam(required = false) Task.Priority priority,
                       @RequestParam(required = false, defaultValue = "createdAt") String sort,
                       @RequestParam(required = false, defaultValue = "DESC") String direction,
                       Model model) {
        Page<Task> tasks = service.list(page, size, status, priority, sort, direction);
        model.addAttribute("tasks", tasks.getContent());
        model.addAttribute("page", tasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sort);
        model.addAttribute("sortDirection", direction);
        return "fragments/task-list :: list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new TaskDto(null, "", "", Task.Priority.MEDIUM, Task.Status.TODO, null));
        return "fragments/task-form :: form";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create(@Valid TaskDto dto, Model model, HttpServletResponse response) {
        service.create(dto);
        response.setHeader("HX-Trigger", "{\"toast\":{\"type\":\"success\",\"message\":\"Task created successfully\"}} ");
        return list(0, 10, null, null, "createdAt", "DESC", model);
    }

    @GetMapping("/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Task task = service.get(id);
        TaskDto dto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus(), task.getDueDate());
        model.addAttribute("task", dto);
        model.addAttribute("activityLogs", activityLogRepository.findByTaskOrderByTimestampDesc(task));
        model.addAttribute("comments", commentRepository.findByTaskOrderByCreatedAtAsc(task));
        return "fragments/task-form :: form";
    }

    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@PathVariable UUID id, @Valid TaskDto dto, Model model, HttpServletResponse response) {
        service.update(id, dto);
        response.setHeader("HX-Trigger", "{\"toast\":{\"type\":\"success\",\"message\":\"Task updated successfully\"}} ");
        return list(0, 10, null, null, "createdAt", "DESC", model);
    }

    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable UUID id, Model model, HttpServletResponse response) {
        service.delete(id);
        response.setHeader("HX-Trigger", "{\"toast\":{\"type\":\"success\",\"message\":\"Task deleted\"}} ");
        return list(0, 10, null, null, "createdAt", "DESC", model);
    }

    // Validation error handler for HTMX: return a toast fragment with error message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(MethodArgumentNotValidException ex, Model model) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    String field = err.getField();
                    String msg = err.getDefaultMessage();
                    return (field != null ? field + ": " : "") + (msg != null ? msg : "Invalid value");
                })
                .distinct()
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Validation error");
        model.addAttribute("message", message);
        model.addAttribute("type", "danger");
        return "fragments/toast :: toast(message=${message}, type=${type})";
    }

    @PostMapping("/{id}/comments")
    public String createComment(@PathVariable UUID id, @RequestParam String text, Model model) {
        Task task = service.get(id);
        at.geise.test.springboot4test.domain.Comment comment = new at.geise.test.springboot4test.domain.Comment(task, text, "User");
        comment = commentRepository.save(comment);

        model.addAttribute("comment", comment);
        return "fragments/task-activity :: comment";
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public String deleteComment(@PathVariable UUID taskId, @PathVariable UUID commentId) {
        commentRepository.deleteById(commentId);
        return "";
    }
}
