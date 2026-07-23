package com.xeon.todolist;

import com.xeon.todolist.controller.TaskController;
import com.xeon.todolist.dto.CreateTaskRequest;
import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.enums.TaskPriority;
import com.xeon.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.completed").value(false))
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
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.priorityWeight").value(2));


        Mockito.verify(taskService, Mockito.times(1)).createTask(any(CreateTaskRequest.class));

    }
}
