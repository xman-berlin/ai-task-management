package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class UiController {

    private final TaskService service;

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size,
                       @RequestParam(required = false) Task.Status status,
                       @RequestParam(required = false) Task.Priority priority,
                       Model model) {
        Page<Task> tasks = service.list(page, size, status, priority);
        model.addAttribute("tasks", tasks.getContent());
        return "fragments/task-list :: list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new TaskDto(null, "", "", Task.Priority.MEDIUM, Task.Status.TODO, null));
        return "fragments/task-form :: form";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create(@Valid TaskDto dto, Model model) {
        service.create(dto);
        return list(0, 20, null, null, model);
    }

    @GetMapping("/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Task task = service.get(id);
        TaskDto dto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus(), task.getDueDate());
        model.addAttribute("task", dto);
        return "fragments/task-form :: form";
    }

    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@PathVariable UUID id, @Valid TaskDto dto, Model model) {
        service.update(id, dto);
        return list(0, 20, null, null, model);
    }

    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable UUID id, Model model) {
        service.delete(id);
        return list(0, 20, null, null, model);
    }
}
