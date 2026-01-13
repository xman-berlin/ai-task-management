package at.geise.test.springboot4test.dto;

import java.time.LocalDateTime;

public record ActivitySummaryDto(
        String action,
        long count,
        LocalDateTime lastActivity
) {
}
