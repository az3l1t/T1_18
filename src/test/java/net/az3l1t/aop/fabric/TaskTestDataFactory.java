package net.az3l1t.aop.fabric;

import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.dto.kafka.KafkaUpdatingDto;
import net.az3l1t.aop.entity.Task;
import net.az3l1t.aop.entity.enumirations.TaskStatus;

public class TaskTestDataFactory {
    public static Task task() {
        return new Task(1L, "Testing task", "Description", 1L, TaskStatus.NEW);
    }

    public static TaskCreateDto taskCreateDto() {
        return new TaskCreateDto("Testing task", "Description", 1L);
    }

    public static TaskResponseDto taskResponseDto() {
        return new TaskResponseDto("Testing task", "Description", 1L);
    }

    public static TaskUpdateDto taskUpdateDto() {
        return new TaskUpdateDto("Testing task", "Description", 1L, TaskStatus.IN_PROGRESS);
    }

    public static TaskUpdateDto taskUpdateDtoNoStatusChange() {
        return new TaskUpdateDto("Testing task", "Description", 1L, TaskStatus.NEW);
    }

    public static KafkaUpdatingDto kafkaUpdatingDto() {
        return KafkaUpdatingDto.builder()
                .taskId(1L)
                .newStatus(TaskStatus.IN_PROGRESS.toString())
                .build();
    }
}
