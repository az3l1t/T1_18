package net.az3l1t.aop.dto.kafka;

public record TaskEventDto(
        Long taskId,
        String title,
        String description,
        Long userId
) {}
