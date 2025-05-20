package net.az3l1t.aop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.az3l1t.aop.config.AbstractTestcontainersConfig;
import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest extends AbstractTestcontainersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_ValidInput_ReturnsCreatedTask() throws Exception {
        TaskCreateDto taskCreateDto = TaskTestDataFactory.taskCreateDto();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(taskCreateDto.title()))
                .andExpect(jsonPath("$.description").value(taskCreateDto.description()))
                .andExpect(jsonPath("$.userId").value(taskCreateDto.userId()));
    }

    @Test
    void createTask_InvalidInput_ReturnsBadRequest() throws Exception {
        TaskCreateDto invalidDto = new TaskCreateDto("", "", 0L);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed for request body"))
                .andExpect(jsonPath("$.details.title").value("Title must be between 3 and 100 character"))
                .andExpect(jsonPath("$.details.userId").value("User ID must be positive"));
    }

    @Test
    void getTaskById_ExistingId_ReturnsTask() throws Exception {
        TaskCreateDto taskCreateDto = TaskTestDataFactory.taskCreateDto();

        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponseDto createdTask = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TaskResponseDto.class);

        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTask.id()))
                .andExpect(jsonPath("$.title").value(createdTask.title()))
                .andExpect(jsonPath("$.description").value(createdTask.description()))
                .andExpect(jsonPath("$.userId").value(createdTask.userId()));
    }

    @Test
    void getTaskById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.details.error").value("Task not found with id: 999"));
    }

    @Test
    void getTaskById_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/tasks/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid parameter"))
                .andExpect(jsonPath("$.details.error").value("Parameter id must be a valid number"));
    }

    @Test
    void getAllTasks_ReturnsPagedTasks() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new TaskCreateDto("Task " + i, "Description " + i, 1L))))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/tasks?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Task 0"))
                .andExpect(jsonPath("$.content[1].title").value("Task 1"))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void updateTask_ValidInput_ReturnsUpdatedTask() throws Exception {
        TaskCreateDto taskCreateDto = TaskTestDataFactory.taskCreateDto();

        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponseDto createdTask = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TaskResponseDto.class);

        TaskUpdateDto updateDto = TaskTestDataFactory.taskUpdateDto();

        mockMvc.perform(put("/tasks/" + createdTask.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTask.id()))
                .andExpect(jsonPath("$.title").value(updateDto.title()))
                .andExpect(jsonPath("$.description").value(updateDto.description()))
                .andExpect(jsonPath("$.userId").value(updateDto.userId()));
    }

    @Test
    void updateTask_NonExistingId_ReturnsNotFound() throws Exception {
        TaskUpdateDto updateDto = TaskTestDataFactory.taskUpdateDto();

        mockMvc.perform(put("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.details.error").value("Task not found with id: 999"));
    }

    @Test
    void updateTask_InvalidInput_ReturnsBadRequest() throws Exception {
        TaskUpdateDto invalidDto = new TaskUpdateDto("", "", 0L, TaskStatus.IN_PROGRESS);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed for request body"))
                .andExpect(jsonPath("$.details.title").value("Title must be between 3 and 100 character"))
                .andExpect(jsonPath("$.details.userId").value("User ID must be positive"));
    }

    @Test
    void deleteTask_ExistingId_ReturnsNoContent() throws Exception {
        TaskCreateDto taskCreateDto = TaskTestDataFactory.taskCreateDto();

        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponseDto createdTask = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TaskResponseDto.class);

        mockMvc.perform(delete("/tasks/" + createdTask.id()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.details.error").value("Task not found with id: 999"));
    }
}
