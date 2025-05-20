package net.az3l1t.aop.dto;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        Long userId
) {}
