package at.geise.test.springboot4test.controller;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.dto.TaskDto;
import at.geise.test.springboot4test.service.TaskService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UiController.class)
class UiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService service;

    private Task testTask;
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
    }

    @Test
    void index_shouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void list_shouldReturnTaskListFragment() throws Exception {
        // Given
        when(service.list(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        // When/Then
        mockMvc.perform(get("/tasks/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/task-list :: list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("page"));

        verify(service).list(0, 10, null, null, "createdAt", "DESC");
    }

    @Test
    void list_shouldAcceptPaginationParameters() throws Exception {
        // Given
        when(service.list(eq(2), eq(20), any(), any(), eq("title"), eq("ASC")))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        // When/Then
        mockMvc.perform(get("/tasks/list")
                        .param("page", "2")
                        .param("size", "20")
                        .param("sort", "title")
                        .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 2))
                .andExpect(model().attribute("pageSize", 20));

        verify(service).list(2, 20, null, null, "title", "ASC");
    }

    @Test
    void createForm_shouldReturnTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/task-form :: form"))
                .andExpect(model().attributeExists("task"));
    }

    @Test
    void create_shouldCreateTaskAndReturnList() throws Exception {
        // Given
        when(service.create(any(TaskDto.class))).thenReturn(testTask);
        when(service.list(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        // When/Then
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Task")
                        .param("description", "Description")
                        .param("priority", "HIGH")
                        .param("status", "TODO")
                        .param("dueDate", LocalDateTime.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(header().exists("HX-Trigger"));

        verify(service).create(any(TaskDto.class));
    }

    @Test
    void create_shouldReturnValidationError_whenTitleMissing() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("description", "Description")
                        .param("priority", "HIGH")
                        .param("status", "TODO"))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(TaskDto.class));
    }

    @Test
    void editForm_shouldReturnTaskFormWithData() throws Exception {
        // Given
        when(service.get(testId)).thenReturn(testTask);

        // When/Then
        mockMvc.perform(get("/tasks/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/task-form :: form"))
                .andExpect(model().attributeExists("task"));

        verify(service).get(testId);
    }

    @Test
    void update_shouldUpdateTaskAndReturnList() throws Exception {
        // Given
        when(service.update(eq(testId), any(TaskDto.class))).thenReturn(testTask);
        when(service.list(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        // When/Then
        mockMvc.perform(post("/tasks/{id}", testId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Updated Task")
                        .param("description", "Updated Description")
                        .param("priority", "LOW")
                        .param("status", "DONE")
                        .param("dueDate", LocalDateTime.now().plusDays(2).toString()))
                .andExpect(status().isOk())
                .andExpect(header().exists("HX-Trigger"));

        verify(service).update(eq(testId), any(TaskDto.class));
    }

    @Test
    void delete_shouldDeleteTaskAndReturnList() throws Exception {
        // Given
        doNothing().when(service).delete(testId);
        when(service.list(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        // When/Then
        mockMvc.perform(post("/tasks/{id}/delete", testId))
                .andExpect(status().isOk())
                .andExpect(header().exists("HX-Trigger"));

        verify(service).delete(testId);
    }
}

