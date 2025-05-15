package net.az3l1t.aop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import net.az3l1t.annotation.LoggingHttp;
import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @LoggingHttp
    public TaskResponseDto createTask(@RequestBody @Valid TaskCreateDto taskCreateDto) {
        return taskService.createTask(taskCreateDto);
    }

    @GetMapping("/{id}")
    @LoggingHttp
    public TaskResponseDto getTaskById(@PathVariable @Positive Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    @LoggingHttp
    public Page<TaskResponseDto> getAllTasks(Pageable pageable) {
        return taskService.getAllTasks(pageable);
    }

    @PutMapping("/{id}")
    @LoggingHttp
    public TaskResponseDto updateTask(@PathVariable @Positive Long id,
                                      @RequestBody @Valid TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(id, taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    @LoggingHttp
    public void deleteTask(@PathVariable @Positive Long id) {
        taskService.deleteTask(id);
    }
}
