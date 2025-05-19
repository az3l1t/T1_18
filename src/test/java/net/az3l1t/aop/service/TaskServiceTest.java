package net.az3l1t.aop.service;

import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.dto.kafka.KafkaUpdatingDto;
import net.az3l1t.aop.entity.Task;
import net.az3l1t.aop.exception.TaskNotFoundException;
import net.az3l1t.aop.fabric.TaskTestDataFactory;
import net.az3l1t.aop.mapper.TaskMapper;
import net.az3l1t.aop.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Spy
    private TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Mock
    private KafkaTemplate<String, KafkaUpdatingDto> taskUpdatingKafkaTemplate;

    @InjectMocks
    private TaskService taskService;

    private static Task task;
    private static TaskCreateDto taskCreateDto;
    private static TaskResponseDto taskResponseDto;
    private static TaskUpdateDto taskUpdateDto;
    private static TaskUpdateDto noStatusChangeDto;

    @BeforeEach
    void setUp() {
        task = TaskTestDataFactory.task();
        taskCreateDto = TaskTestDataFactory.taskCreateDto();
        taskResponseDto = TaskTestDataFactory.taskResponseDto();
        taskUpdateDto = TaskTestDataFactory.taskUpdateDto();
        noStatusChangeDto = TaskTestDataFactory.taskUpdateDtoNoStatusChange();

        ReflectionTestUtils.setField(taskService, "taskUpdatingTopic", "test-topic");
    }

    @Test
    void createTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDto result = taskService.createTask(taskCreateDto);

        assertNotNull(result);
        assertEquals(taskResponseDto.title(), result.title());
        assertEquals(taskResponseDto.description(), result.description());
        assertEquals(taskResponseDto.userId(), result.userId());
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toEntity(taskCreateDto);
        verify(taskMapper).toResponseDto(any(Task.class));
    }

    @Test
    void updateTask_Success_WithStatusChange() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponseDto result = taskService.updateTask(1L, taskUpdateDto);

        assertNotNull(result);
        assertEquals(taskResponseDto.title(), result.title());
        assertEquals(taskResponseDto.description(), result.description());
        assertEquals(taskResponseDto.userId(), result.userId());
        verify(taskMapper).updateEntityFromDto(taskUpdateDto, task);
        verify(taskUpdatingKafkaTemplate).send(anyString(), any(KafkaUpdatingDto.class));
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_Success_NoStatusChange() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDto result = taskService.updateTask(1L, noStatusChangeDto);

        assertNotNull(result);
        assertEquals(taskResponseDto.title(), result.title());
        assertEquals(taskResponseDto.description(), result.description());
        assertEquals(taskResponseDto.userId(), result.userId());
        verify(taskMapper).updateEntityFromDto(noStatusChangeDto, task);
        verify(taskUpdatingKafkaTemplate, never()).send(anyString(), any(KafkaUpdatingDto.class));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, taskUpdateDto));
        verify(taskMapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(taskResponseDto.title(), result.title());
        assertEquals(taskResponseDto.description(), result.description());
        assertEquals(taskResponseDto.userId(), result.userId());
        verify(taskRepository).findById(1L);
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    void getTaskById_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskMapper, never()).toResponseDto(any());
    }

    @Test
    void getAllTasks_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        Page<TaskResponseDto> result = taskService.getAllTasks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(taskResponseDto.title(), result.getContent().get(0).title());
        assertEquals(taskResponseDto.description(), result.getContent().get(0).description());
        assertEquals(taskResponseDto.userId(), result.getContent().get(0).userId());
        verify(taskRepository).findAll(pageable);
        verify(taskMapper).toResponseDto(task);
    }
}
