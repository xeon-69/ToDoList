package com.xeon.todolist;

import com.xeon.todolist.controller.TaskController;
import com.xeon.todolist.dto.CreateTaskRequest;
import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.dto.UpdateTaskRequest;
import com.xeon.todolist.enums.TaskPriority;
import com.xeon.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTask_withJustTitle() throws Exception {

        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder().title("Task1").build();

        TaskResponse taskResponse = TaskResponse.builder().title("Task1").taskId(1L).isCompleted(false).priority(TaskPriority.MEDIUM).priorityWeight(2).build();

        Mockito.when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task1"))
                .andExpect(jsonPath("$.isCompleted").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.priorityWeight").value(2));

        Mockito.verify(taskService, Mockito.times(1)).createTask(any(CreateTaskRequest.class));

    }

    @Test
    void shouldCreateTask_withPriority() throws Exception {

        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder().title("Task1").priority(TaskPriority.MEDIUM).build();

        TaskResponse taskResponse = TaskResponse.builder().title("Task1").taskId(1L).isCompleted(false).priority(TaskPriority.MEDIUM).priorityWeight(2).build();

        Mockito.when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task1"))
                .andExpect(jsonPath("$.isCompleted").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.priorityWeight").value(2));


        Mockito.verify(taskService, Mockito.times(1)).createTask(any(CreateTaskRequest.class));

    }

    @Test
    void shouldUpdateTask_withId() throws Exception {

        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder().title("New Title").isCompleted(true).priority(TaskPriority.MEDIUM).build();

        TaskResponse taskResponse = TaskResponse.builder().taskId(1L).title("New Title").isCompleted(true).priority(TaskPriority.MEDIUM).priorityWeight(2).build();

        Mockito.when(taskService.updateTask(Mockito.anyLong(), any(UpdateTaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTaskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.isCompleted").value(true))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.priorityWeight").value(2));

        Mockito.verify(taskService).updateTask(Mockito.eq(1L), any());
    }

    @Test
    void shouldDeleteTask_withId() throws Exception {
        long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);
        mockMvc.perform(delete("/api/tasks/{id}", taskId)).andExpect(status().isNoContent());
        Mockito.verify(taskService, Mockito.times(1)).deleteTask(taskId);

    }

    @Test
    void shouldReturnTask_withId() throws Exception {
        long taskId = 1L;
        TaskResponse taskResponse = TaskResponse.builder().taskId(1L).title("Title").isCompleted(true).priority(TaskPriority.MEDIUM).priorityWeight(2).build();
        Mockito.when(taskService.getTask(Mockito.anyLong())).thenReturn(taskResponse);
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.isCompleted").value(true))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.priorityWeight").value(2));
        Mockito.verify(taskService, Mockito.times(1)).getTask(Mockito.anyLong());
    }

    @Test
    void shouldReturnPaginatedTasksByPage_DefaultParams() throws Exception {
        TaskResponse taskResponse1 = TaskResponse.builder().taskId(1L).title("Task1").build();
        TaskResponse taskResponse2 = TaskResponse.builder().taskId(2L).title("Task2").build();

        List<TaskResponse> taskResponseList = List.of(taskResponse1, taskResponse2);
        Page<TaskResponse> mockPage = new PageImpl<>(taskResponseList, PageRequest.of(0, 10), taskResponseList.size());

        Mockito.when(taskService.getAllTasksByPage(Mockito.eq(0), Mockito.eq(10), Mockito.eq(List.of("id")))).thenReturn(mockPage);
        mockMvc.perform(get("/api/tasks").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].taskId").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Task1"))
                .andExpect(jsonPath("$.content[1].taskId").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Task2"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
        Mockito.verify(taskService, Mockito.times(1)).getAllTasksByPage(0, 10, List.of("id"));
    }

    @Test
    void shouldReturnPaginatedTasksByPage_CustomParams() throws Exception {
        TaskResponse taskResponse1 = TaskResponse.builder().taskId(6L).title("Task6").build();
        TaskResponse taskResponse2 = TaskResponse.builder().taskId(7L).title("Task7").build();

        List<TaskResponse> taskResponseList = List.of(taskResponse1, taskResponse2);
        Page<TaskResponse> mockPage = new PageImpl<>(taskResponseList, PageRequest.of(1, 5), 7);

        Mockito.when(taskService.getAllTasksByPage(Mockito.eq(1), Mockito.eq(5), Mockito.eq(List.of("title", "asc")))).thenReturn(mockPage);
        mockMvc.perform(get("/api/tasks")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "title", "asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].taskId").value(6))
                .andExpect(jsonPath("$.content[0].title").value("Task6"))
                .andExpect(jsonPath("$.content[1].taskId").value(7))
                .andExpect(jsonPath("$.content[1].title").value("Task7"))
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5));
        Mockito.verify(taskService, Mockito.times(1)).getAllTasksByPage(1, 5, List.of("title", "asc"));
    }
}
