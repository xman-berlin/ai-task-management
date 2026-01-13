package at.geise.test.springboot4test.dto;

public record CreateCommentRequest(
        String content,
        String author
) {
}
