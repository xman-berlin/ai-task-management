package at.geise.test.springboot4test.service;

import at.geise.test.springboot4test.domain.Comment;
import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.CommentDto;
import at.geise.test.springboot4test.dto.CreateCommentRequest;
import at.geise.test.springboot4test.dto.UpdateCommentRequest;
import at.geise.test.springboot4test.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final ActivityLogService activityLogService;

    public List<CommentDto> getCommentsForTask(UUID taskId) {
        Task task = taskService.get(taskId);
        return commentRepository.findByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(UUID taskId, CreateCommentRequest request) {
        Task task = taskService.get(taskId);

        Comment comment = new Comment(task, request.content(), request.author());
        comment = commentRepository.save(comment);

        // Log the activity
        activityLogService.logCommentAdded(task, request.author());

        return toDto(comment);
    }

    public CommentDto updateComment(UUID commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        comment.setContent(request.content());
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return toDto(comment);
    }

    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        commentRepository.delete(comment);
    }

    public long getCommentCountForTask(UUID taskId) {
        Task task = taskService.get(taskId);
        return commentRepository.countByTask(task);
    }

    private CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getTask().getId(),
                comment.getContent(),
                comment.getAuthor(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}

