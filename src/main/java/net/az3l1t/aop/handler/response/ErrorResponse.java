package net.az3l1t.aop.handler.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> details
) {
    public ErrorResponse(int status, String error, String message, Map<String, String> details) {
        this(LocalDateTime.now(), status, error, message, details);
    }
}
