package com.xeon.todolist;

import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.dto.UpdateTaskRequest;
import com.xeon.todolist.entity.Tasks;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.exception.TaskAccessDeniedException;
import com.xeon.todolist.exception.TaskNotFoundException;
import com.xeon.todolist.mapper.TaskMapper;
import com.xeon.todolist.repository.TaskRepository;
import com.xeon.todolist.service.TaskService;
import com.xeon.todolist.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private SecurityUtil securityUtil;

    @Test
    void shouldThrowException_WhenTaskIsNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.getTask(1L));

        assertEquals("Task with ID: " + 1 + " not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowException_WhenUserIsNotValidForTheTask(){
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).user(user).title("title").build();
        Users loggedInUser = Users.builder().username("differentUser").password("differentPassword").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(securityUtil.getCurrentUser()).thenReturn(loggedInUser);
        TaskAccessDeniedException exception = assertThrows(TaskAccessDeniedException.class, () -> taskService.getTaskAndValidateOwnership(1L));

        assertEquals("Task access denied", exception.getMessage());
        verify(securityUtil).getCurrentUser();
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldDeleteTheTask_WhenExists(){
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        taskService.deleteTask(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldUpdateTheTask_WhenExists(){
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).build();

        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder().title("newTitle").isCompleted(true).build();

        TaskResponse expectedTaskResponse = TaskResponse.builder().title("newTitle").isCompleted(true).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(taskMapper.toResponse(task)).thenReturn(expectedTaskResponse);
        when(taskRepository.save(any(Tasks.class))).thenReturn(task);

        TaskResponse taskResponse = taskService.updateTask(task.getId(), updateTaskRequest);

        assertEquals(expectedTaskResponse, taskResponse);

        verify(taskRepository).findById(1L);
        verify(taskRepository).saveAndFlush(task);
        verify(taskMapper).toResponse(task);
    }




}
