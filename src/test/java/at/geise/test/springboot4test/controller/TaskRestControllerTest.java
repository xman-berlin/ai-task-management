package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskRestController.class)
class TaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;
    private TaskDto testDto;
    private UUID testId;

    @BeforeEach
    void setUp() {

        testId = UUID.randomUUID();
        testTask = new Task();
        testTask.setId(testId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setPriority(Task.Priority.HIGH);
        testTask.setStatus(Task.Status.TODO);
        testTask.setDueDate(LocalDateTime.now().plusDays(1));
        testTask.setCreatedAt(LocalDateTime.now());

        testDto = new TaskDto(
                testId,
                "Test Task",
                "Test Description",
                Task.Priority.HIGH,
                Task.Status.TODO,
                LocalDateTime.now().plusDays(1)
        );
    }

    @Test
    void list_shouldReturnPageOfTasks() throws Exception {
        // Given
        when(service.list(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        // When/Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));

        verify(service).list(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_shouldAcceptFilterParameters() throws Exception {
        // Given
        when(service.list(eq(1), eq(20), eq(Task.Status.TODO), eq(Task.Priority.HIGH), eq("title"), eq("ASC")))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        // When/Then
        mockMvc.perform(get("/api/tasks")
                        .param("page", "1")
                        .param("size", "20")
                        .param("status", "TODO")
                        .param("priority", "HIGH")
                        .param("sort", "title")
                        .param("direction", "ASC"))
                .andExpect(status().isOk());

        verify(service).list(1, 20, Task.Status.TODO, Task.Priority.HIGH, "title", "ASC");
    }

    @Test
    void get_shouldReturnTask_whenExists() throws Exception {
        // Given
        when(service.get(testId)).thenReturn(testTask);

        // When/Then
        mockMvc.perform(get("/api/tasks/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(service).get(testId);
    }

    @Test
    void get_shouldReturn404_whenNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(service.get(nonExistentId)).thenThrow(new IllegalArgumentException("Task not found"));

        // When/Then
        mockMvc.perform(get("/api/tasks/{id}", nonExistentId))
                .andExpect(status().isBadRequest());

        verify(service).get(nonExistentId);
    }

    @Test
    void create_shouldReturnCreatedTask() throws Exception {
        // Given
        when(service.create(any(TaskDto.class))).thenReturn(testTask);

        // When/Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(service).create(any(TaskDto.class));
    }

    @Test
    void create_shouldValidateRequiredFields() throws Exception {
        // Given
        TaskDto invalidDto = new TaskDto(null, null, null, null, null, null);

        // When/Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(TaskDto.class));
    }

    @Test
    void update_shouldReturnUpdatedTask() throws Exception {
        // Given
        when(service.update(eq(testId), any(TaskDto.class))).thenReturn(testTask);

        // When/Then
        mockMvc.perform(put("/api/tasks/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(service).update(eq(testId), any(TaskDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(service).delete(testId);

        // When/Then
        mockMvc.perform(delete("/api/tasks/{id}", testId))
                .andExpect(status().isNoContent());

        verify(service).delete(testId);
    }
}

