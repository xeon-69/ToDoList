package com.xeon.todolist.service;

import com.xeon.todolist.dto.CreateTaskRequest;
import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.dto.UpdateTaskRequest;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.exception.TaskNotFoundException;
import com.xeon.todolist.exception.UserNotFoundException;
import com.xeon.todolist.mapper.TaskMapper;
import com.xeon.todolist.entity.Tasks;
import com.xeon.todolist.repository.TaskRepository;
import com.xeon.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskResponse createTask(CreateTaskRequest createTaskRequest) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Users user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));

        Tasks task = new Tasks();
        task.setTitle(createTaskRequest.getTitle());
        task.setPriority(createTaskRequest.getPriority());
        task.setUser(user);
        return TaskMapper.toResponse(taskRepository.save(task));
    }

//    public List<TaskResponse> getAllTasks() {
//        return taskRepository.findAll().stream().map(TaskMapper::toResponse).toList();
//    }

    public void deleteTask(long taskId) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    public TaskResponse updateTask(long taskId, UpdateTaskRequest updateTaskRequest) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setTitle(updateTaskRequest.getTitle());
        task.setId(taskId);
        task.setCompleted(updateTaskRequest.isCompleted());
        task.setPriority(updateTaskRequest.getPriority());
        return TaskMapper.toResponse(task);
    }

    public TaskResponse toggleTaskStatus(long taskId) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setCompleted(!task.isCompleted());
        return TaskMapper.toResponse(task);
    }

    public TaskResponse getTask(long taskId) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return TaskMapper.toResponse(task);
    }

    public Page<TaskResponse> getAllTasksByPage(int page, int size, List<String> sort) {

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        return taskRepository.findAll(pageable).map(TaskMapper::toResponse);
    }


    public Sort parseSort(List<String> sortParam) {
        if (sortParam.isEmpty()) {
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
