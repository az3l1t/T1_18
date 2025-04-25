package net.az3l1t.aop.dto;

public record TaskResponseDto(
        String title,
        String description,
        Long userId
) {}
