package net.az3l1t.aop.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import net.az3l1t.aop.entity.enumirations.TaskStatus;

public record TaskUpdateDto(
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 character")
        String title,
        @Size(max = 500, message = "Description must be less 500 character")
        String description,
        @Positive(message = "User ID must be positive")
        Long userId,
        TaskStatus status
) {}
