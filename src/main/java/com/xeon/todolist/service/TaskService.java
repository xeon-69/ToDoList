package com.xeon.todolist.service;

import com.xeon.todolist.dto.CreateTaskRequest;
import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.dto.UpdateTaskRequest;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.exception.DuplicateTaskException;
import com.xeon.todolist.exception.TaskAccessDeniedException;
import com.xeon.todolist.exception.TaskNotFoundException;
import com.xeon.todolist.mapper.TaskMapper;
import com.xeon.todolist.entity.Tasks;
import com.xeon.todolist.repository.TaskRepository;
import com.xeon.todolist.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final SecurityUtil securityUtil;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest createTaskRequest) {

        Users user = securityUtil.getCurrentUser();
        log.debug("Creating task for the user {}", user.getUsername());
        if (taskRepository.existsByTitleAndUser(createTaskRequest.getTitle(), user)) {
            log.warn("User '{}' attempted to create a duplicate task title: '{}'",
                    user.getUsername(), createTaskRequest.getTitle());
            throw new DuplicateTaskException("A task with this title already exists.");
        }
        Tasks task = new Tasks(); // need save() since it creates a new entity
        task.setTitle(createTaskRequest.getTitle());
        task.setPriority(createTaskRequest.getPriority());
        task.setUser(user);
        Tasks createdTask = taskRepository.save(task);
        log.info("Task ID: {} created for the user {}", createdTask.getId(), user.getUsername());
        return taskMapper.toResponse(createdTask);
    }

    @Transactional
    public void deleteTask(long taskId) {
        Users user = securityUtil.getCurrentUser();
        log.debug("Deleting task for the user {}", user.getUsername());
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            log.warn("Accessing to task ID: {} denied for the user {} ", taskId, user.getUsername());
            throw new TaskAccessDeniedException("Task Access Denied");
        }
        taskRepository.delete(task);
        log.info("Task ID: {} has been deleted", taskId);
    }

    @Transactional
    public TaskResponse updateTask(long taskId, UpdateTaskRequest updateTaskRequest) {
        log.debug("Searching for the task...");
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        //check user first
        Users user = securityUtil.getCurrentUser();
        log.debug("Updating task ID: {} for the user {}", task.getId(), user.getUsername());
        if (!task.getUser().getId().equals(user.getId())) {
            log.warn("Accessed denied for the user {} ", user.getUsername());
            throw new TaskAccessDeniedException("Task Access Denied");
        }
        task.setTitle(updateTaskRequest.getTitle());
        task.setCompleted(updateTaskRequest.isCompleted());
        task.setPriority(updateTaskRequest.getPriority());
        Tasks updatedTask = taskRepository.saveAndFlush(task);
        log.info("Task ID: {} has been updated", updatedTask.getId());
        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public TaskResponse toggleTaskStatus(long taskId) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        Users user = securityUtil.getCurrentUser();
        if (!task.getUser().getId().equals(user.getId())) {
            throw new TaskAccessDeniedException("Task Access Denied");
        }
        task.setCompleted(!task.isCompleted());
        return taskMapper.toResponse(task);
    }

    public TaskResponse getTask(long taskId) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        Users user = securityUtil.getCurrentUser();
        if (!task.getUser().getId().equals(user.getId())) {
            throw new TaskAccessDeniedException("Task Access Denied");
        }
        return taskMapper.toResponse(task);
    }

    public Page<TaskResponse> getAllTasksByPage(int page, int size, List<String> sort) {

        Users user = securityUtil.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        return taskRepository.findTasksByUsername(user.getUsername(), pageable).map(taskMapper::toResponse);
    }


    public Sort parseSort(List<String> sortParam) {
        if (sortParam == null || sortParam.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "priorityWeight");
        }

        //  ["priority,desc","title,asc"]
        List<Sort.Order> orders = new ArrayList<>();

        for (String s : sortParam) {
            String[] parts = s.split(",");
            String field = parts[0];
            Sort.Direction direction = (parts.length > 1 &&
                    parts[1].equalsIgnoreCase("desc")) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, field));
        }
        return Sort.by(orders);
    }

}
