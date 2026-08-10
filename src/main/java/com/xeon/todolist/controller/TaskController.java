package com.xeon.todolist.controller;

import com.xeon.todolist.dto.CreateTaskRequest;
import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.dto.UpdateTaskRequest;
import com.xeon.todolist.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CreateTaskRequest createTaskRequest
    ) {
        TaskResponse taskResponse = taskService.createTask(createTaskRequest);
        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> update(
            @PathVariable long taskId,
            @RequestBody UpdateTaskRequest updateTaskRequest
    ) {
        TaskResponse taskResponse = taskService.updateTask(
                taskId,
                updateTaskRequest
        );
        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping("/{taskId}/toggle")
    public ResponseEntity<?> toggleTask(@PathVariable long taskId) {
        TaskResponse taskResponse = taskService.toggleTaskStatus(taskId);
        return ResponseEntity.ok(taskResponse);
    }

    //    @GetMapping
    //    public ResponseEntity<List<TaskResponse>> getAllTasks() {
    //        return ResponseEntity.ok(taskService.getAllTasks());
    //    }

    //    @GetMapping
    //    public ResponseEntity<Page<TaskResponse>> getTasksByPage(
    //            @RequestParam(defaultValue = "0") int page,
    //            @RequestParam(defaultValue = "10") int size,
    //            @RequestParam List<String> sort
    //    ) {
    //        Page<TaskResponse> pageResponse = taskService.getAllTasksByPage(page, size, sort);
    //        return ResponseEntity.ok(pageResponse);
    //    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasksByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sort
    ) {
        if (sort == null) {
            sort = List.of("id"); // default sorting
        }

        Page<TaskResponse> pageResponse = taskService.getAllTasksByPage(
                page,
                size,
                sort
        );

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable long taskId) {
        TaskResponse taskResponse = taskService.getTask(taskId);
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
