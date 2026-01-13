package at.geise.test.springboot4test.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityLogDto(
        UUID id,
        UUID taskId,
        String action,
        String oldValue,
        String newValue,
        String author,
        LocalDateTime timestamp
) {
}
