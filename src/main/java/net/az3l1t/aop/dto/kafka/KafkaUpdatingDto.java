package net.az3l1t.aop.dto.kafka;

import lombok.Builder;

@Builder
public record KafkaUpdatingDto(
        Long taskId,
        String newStatus
) {}
