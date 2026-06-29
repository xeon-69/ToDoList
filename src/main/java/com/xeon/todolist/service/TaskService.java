package com.xeon.todolist.service;

import com.xeon.todolist.dto.CreateTaskRequest;
import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.dto.UpdateTaskRequest;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.exception.TaskAccessDeniedException;
import com.xeon.todolist.exception.TaskNotFoundException;
import com.xeon.todolist.exception.UserNotFoundException;
import com.xeon.todolist.mapper.TaskMapper;
import com.xeon.todolist.entity.Tasks;
import com.xeon.todolist.repository.TaskRepository;
import com.xeon.todolist.repository.UserRepository;
import com.xeon.todolist.security.UserPrincipal;
import com.xeon.todolist.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final SecurityUtil securityUtil;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest createTaskRequest) {
        Users user = securityUtil.getCurrentUser();
        Tasks task = new Tasks(); // need save() since it creates a new entity
        task.setTitle(createTaskRequest.getTitle());
        task.setPriority(createTaskRequest.getPriority());
        task.setUser(user);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(long taskId) {
        Users user = securityUtil.getCurrentUser();
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())){
            throw new TaskAccessDeniedException("Task Access Denied");
        }
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse updateTask(long taskId, UpdateTaskRequest updateTaskRequest) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        //check user first
        Users user = securityUtil.getCurrentUser();
        if (!task.getUser().getId().equals(user.getId())) {
            throw new TaskAccessDeniedException("Task Access Denied");
        }
        task.setTitle(updateTaskRequest.getTitle());
        task.setCompleted(updateTaskRequest.isCompleted());
        task.setPriority(updateTaskRequest.getPriority());
        return taskMapper.toResponse(task);
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
