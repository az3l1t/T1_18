package net.az3l1t.aop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.dto.kafka.KafkaUpdatingDto;
import net.az3l1t.aop.entity.Task;
import net.az3l1t.aop.entity.enumirations.TaskStatus;
import net.az3l1t.aop.exception.TaskNotFoundException;
import net.az3l1t.aop.mapper.TaskMapper;
import net.az3l1t.aop.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final KafkaTemplate<String, KafkaUpdatingDto> taskUpdatingKafkaTemplate;

    @Value("${kafka.topics.task-updating}")
    private String taskUpdatingTopic;

    @Transactional
    public TaskResponseDto createTask(TaskCreateDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        task.setStatus(TaskStatus.NEW);
        return taskMapper.toResponseDto(taskRepository.save(task));
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskUpdateDto taskUpdateDto) {
        Task task = findTaskById(id);
        TaskStatus oldStatus = task.getStatus();
        taskMapper.updateEntityFromDto(taskUpdateDto, task);

        if (taskUpdateDto.status() != null && !taskUpdateDto.status().equals(oldStatus)) {
            taskRepository.save(task);
            KafkaUpdatingDto taskUpdatingDto = KafkaUpdatingDto.builder()
                    .taskId(task.getId())
                    .newStatus(taskUpdateDto.status().toString())
                    .build();
            taskUpdatingKafkaTemplate.send(taskUpdatingTopic, taskUpdatingDto);
            log.debug("Updating task status: {}, sent to topic: {}",
                    taskUpdateDto.status(), taskUpdatingTopic);
        }

        return taskMapper.toResponseDto(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = findTaskById(id);
        return taskMapper.toResponseDto(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toResponseDto);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }
}
