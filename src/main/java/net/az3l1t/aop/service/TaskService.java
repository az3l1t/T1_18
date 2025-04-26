package net.az3l1t.aop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.az3l1t.aop.aspect.annotation.*;
import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.entity.Task;
import net.az3l1t.aop.exception.TaskNotFoundException;
import net.az3l1t.aop.mapper.TaskMapper;
import net.az3l1t.aop.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional
    @LogExecution
    public TaskResponseDto createTask(TaskCreateDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        return taskMapper.toResponseDto(taskRepository.save(task));
    }

    @Transactional
    @LogExceptionTaskNotFound
    @LogExecution
    public TaskResponseDto updateTask(Long id, TaskUpdateDto taskUpdateDto) {
        Task task = findTaskById(id);
        taskMapper.updateEntityFromDto(taskUpdateDto, task);
        return taskMapper.toResponseDto(task);
    }

    @Transactional
    @LogExceptionTaskNotFound
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @LogExceptionTaskNotFound
    @LogResult
    @LogExecution
    public TaskResponseDto getTaskById(Long id) {
        Task task = findTaskById(id);
        return taskMapper.toResponseDto(task);
    }

    @Transactional(readOnly = true)
    @LogTracking
    @LogExecution
    public Page<TaskResponseDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toResponseDto);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }
}
