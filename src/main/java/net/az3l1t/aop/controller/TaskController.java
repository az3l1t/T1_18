package net.az3l1t.aop.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
    public TaskResponseDto createTask(@RequestBody TaskCreateDto taskCreateDto) {
        return taskService.createTask(taskCreateDto);
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable @Positive Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    public Page<TaskResponseDto> getAllTasks(Pageable pageable) {
        return taskService.getAllTasks(pageable);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable @Positive Long id,
                                      @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(id, taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable @Positive Long id) {
        taskService.deleteTask(id);
    }
}
