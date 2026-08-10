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
import org.springframework.data.domain.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void shouldThrowException_WhenTaskIsNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.getTask(1L));

        assertEquals("Task with ID: " + 1 + " not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowException_WhenUserIsNotValidForTheTask() {
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).user(user).title("title").build();
        Users loggedInUser = Users.builder().id(2L).username("differentUser").password("differentPassword").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(securityUtil.getCurrentUser()).thenReturn(loggedInUser);
        TaskAccessDeniedException exception = assertThrows(TaskAccessDeniedException.class, () -> taskService.getTaskAndValidateOwnership(1L));

        assertEquals("Task access denied", exception.getMessage());
        verify(securityUtil).getCurrentUser();
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldDeleteTheTask_WhenExists() {
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        taskService.deleteTask(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldUpdateTheTask_WhenExists() {
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).build();

        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder().title("newTitle").isCompleted(true).build();

        TaskResponse expectedTaskResponse = TaskResponse.builder().taskId(1L).title("newTitle").isCompleted(true).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(taskMapper.toResponse(task)).thenReturn(expectedTaskResponse);

        //return the same object (invocation) | good practice to use it on save()/saveAndFlush() methods
        when(taskRepository.saveAndFlush(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TaskResponse taskResponse = taskService.updateTask(task.getId(), updateTaskRequest);

        assertEquals(expectedTaskResponse, taskResponse);

        verify(taskRepository).findById(1L);
        verify(taskRepository).saveAndFlush(task);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldToggleTask() {
        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).isCompleted(false).build();

        // mock the expected response to compare later
        TaskResponse exceptedTaskResponse = TaskResponse.builder().title("title").isCompleted(true).userId(1L).build();


        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(taskRepository.saveAndFlush(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskMapper.toResponse(any(Tasks.class)))
                .thenReturn(exceptedTaskResponse);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));


        TaskResponse taskResponse = taskService.toggleTaskStatus(1L);

        assertTrue(taskResponse.getIsCompleted());
        assertEquals(exceptedTaskResponse, taskResponse);

        verify(taskRepository).saveAndFlush(task);
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldReturnPaginatedTasksWithDefaultSorting() {

        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).build();

        TaskResponse expectedTaskResponse = TaskResponse.builder().taskId(1L).title("title").build();

        List<Tasks> tasksList = List.of(task);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priorityWeight"));
        Page<Tasks> mockedPage = new PageImpl<>(tasksList, pageable, tasksList.size());

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(taskRepository.findTasksByUsername("username", pageable)).thenReturn(mockedPage);
        when(taskMapper.toResponse(task)).thenReturn(expectedTaskResponse);

        Page<TaskResponse> result = taskService.getAllTasksByPage(0, 10, null);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(10, result.getSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "priorityWeight"), result.getSort());
        assertEquals("title", result.getContent().getFirst().getTitle());

        verify(taskRepository).findTasksByUsername("username", pageable);
        verify(securityUtil).getCurrentUser();
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldReturnPaginatedTasksWithCustomSorting() {

        Users user = Users.builder().id(1L).username("username").password("password").build();
        Tasks task = Tasks.builder().id(1L).title("title").user(user).build();

        TaskResponse expectedTaskResponse = TaskResponse.builder().taskId(1L).title("title").build();

        List<Tasks> tasksList = List.of(task);

        List<String> customSort = List.of("priorityWeight,desc","title,asc");

        Sort expectedSort = Sort.by(
                new Sort.Order(Sort.Direction.DESC, "priorityWeight"),
                new Sort.Order(Sort.Direction.ASC, "title"));

        Pageable pageable = PageRequest.of(0, 10, expectedSort);
        Page<Tasks> mockedPage = new PageImpl<>(tasksList, pageable, tasksList.size());

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(taskRepository.findTasksByUsername("username", pageable)).thenReturn(mockedPage);
        when(taskMapper.toResponse(task)).thenReturn(expectedTaskResponse);

        Page<TaskResponse> result = taskService.getAllTasksByPage(0, 10, customSort);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(10, result.getSize());
        assertEquals(expectedSort, result.getSort());
        assertEquals("title", result.getContent().getFirst().getTitle());

        verify(taskRepository).findTasksByUsername("username", pageable);
        verify(securityUtil).getCurrentUser();
        verify(taskMapper).toResponse(task);
    }


}