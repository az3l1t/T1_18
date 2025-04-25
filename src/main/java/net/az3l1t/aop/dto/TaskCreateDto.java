package net.az3l1t.aop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TaskCreateDto(
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 character")
        String title,
        @Size(max = 500, message = "Description must be less 500 character")
        String description,
        @NotNull(message = "User ID must be here")
        @Positive(message = "User ID must be positive")
        Long userId
) {}
