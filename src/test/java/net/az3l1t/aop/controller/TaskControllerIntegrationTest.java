package net.az3l1t.aop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.az3l1t.aop.config.AbstractTestcontainersConfig;
import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.entity.Task;
import net.az3l1t.aop.entity.enumirations.TaskStatus;
import net.az3l1t.aop.fabric.TaskTestDataFactory;
import net.az3l1t.aop.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest extends AbstractTestcontainersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_Success() throws Exception {
        TaskCreateDto taskCreateDto = TaskTestDataFactory.taskCreateDto();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(taskCreateDto.title())))
                .andExpect(jsonPath("$.description", is(taskCreateDto.description())))
                .andExpect(jsonPath("$.userId", is(taskCreateDto.userId().intValue())));

        List<Task> tasks = taskRepository.findAll();
        assert tasks.size() == 1;
        assert tasks.get(0).getTitle().equals(taskCreateDto.title());
        assert tasks.get(0).getStatus() == TaskStatus.NEW;
    }

    @Test
    void createTask_InvalidInput_BadRequest() throws Exception {
        TaskCreateDto invalidDto = new TaskCreateDto("", "", -1L);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        assert taskRepository.findAll().isEmpty();
    }

    @Test
    void updateTask_Success_WithStatusChange() throws Exception {
        Task task = TaskTestDataFactory.task();
        taskRepository.save(task);
        TaskUpdateDto taskUpdateDto = TaskTestDataFactory.taskUpdateDto();

        mockMvc.perform(put("/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(taskUpdateDto.title())))
                .andExpect(jsonPath("$.description", is(taskUpdateDto.description())))
                .andExpect(jsonPath("$.userId", is(taskUpdateDto.userId().intValue())));

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        assert updatedTask.getStatus() == TaskStatus.IN_PROGRESS;
    }

    @Test
    void updateTask_TaskNotFound_NotFound() throws Exception {
        TaskUpdateDto taskUpdateDto = TaskTestDataFactory.taskUpdateDto();

        mockMvc.perform(put("/tasks/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_Success() throws Exception {
        Task task = TaskTestDataFactory.task();
        taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        assert taskRepository.findById(task.getId()).isEmpty();
    }

    @Test
    void deleteTask_TaskNotFound_NotFound() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_Success() throws Exception {
        Task task = TaskTestDataFactory.task();
        taskRepository.save(task);

        mockMvc.perform(get("/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(task.getTitle())))
                .andExpect(jsonPath("$.description", is(task.getDescription())))
                .andExpect(jsonPath("$.userId", is(task.getUserId().intValue())));
    }

    @Test
    void getTaskById_TaskNotFound_NotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_Success() throws Exception {
        Task task1 = TaskTestDataFactory.task();
        Task task2 = new Task(2L, "Another task", "Another description", 2L, TaskStatus.NEW);
        taskRepository.saveAll(List.of(task1, task2));

        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(task1.getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(task2.getTitle())));
    }

    @Test
    void getAllTasks_EmptyList_Success() throws Exception {
        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
