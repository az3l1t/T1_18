package net.az3l1t.aop.handler.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        String message,
        Map<String, String> details
) {
    public ErrorResponse(String message, Map<String, String> details) {
        this(LocalDateTime.now(), message, details);
    }
}
