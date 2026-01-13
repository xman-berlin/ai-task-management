package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.*;
import at.geise.test.springboot4test.service.CommentService;
import at.geise.test.springboot4test.service.TaskService;
import at.geise.test.springboot4test.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService service;
    private final CommentService commentService;
    private final ActivityLogService activityLogService;

    @GetMapping
    public Page<Task> list(@RequestParam(required = false) Integer page,
                           @RequestParam(required = false) Integer size,
                           @RequestParam(required = false) Task.Status status,
                           @RequestParam(required = false) Task.Priority priority,
                           @RequestParam(required = false, defaultValue = "createdAt") String sort,
                           @RequestParam(required = false, defaultValue = "DESC") String direction) {
        return service.list(page, size, status, priority, sort, direction);
    }

    @GetMapping("/{id}")
    public Task get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody @Valid TaskDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable UUID id, @RequestBody @Valid TaskDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    // Comment endpoints
    @GetMapping("/{taskId}/comments")
    public List<CommentDto> getComments(@PathVariable UUID taskId) {
        return commentService.getCommentsForTask(taskId);
    }

    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable UUID taskId, @RequestBody @Valid CreateCommentRequest request) {
        return commentService.addComment(taskId, request);
    }

    @PutMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable UUID commentId, @RequestBody @Valid UpdateCommentRequest request) {
        return commentService.updateComment(commentId, request);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
    }

    // Activity log endpoints
    @GetMapping("/{taskId}/activity")
    public List<ActivityLogDto> getActivity(@PathVariable UUID taskId) {
        return activityLogService.getActivityForTask(taskId);
    }

    @GetMapping("/{taskId}/activity/paged")
    public Page<ActivityLogDto> getActivityPaged(@PathVariable UUID taskId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return activityLogService.getActivityForTask(taskId, page, size);
    }

    @GetMapping("/activity/recent")
    public List<ActivityLogDto> getRecentActivity(@RequestParam(defaultValue = "20") int limit) {
        return activityLogService.getRecentActivity(limit);
    }

    @GetMapping("/{taskId}/activity/summary")
    public List<ActivitySummaryDto> getActivitySummary(@PathVariable UUID taskId) {
        return activityLogService.getActivitySummary(taskId);
    }
}
