package com.xeon.todolist.dto;

import com.xeon.todolist.enums.TaskPriority;
import jakarta.annotation.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTaskRequest {
    private String title;
    private TaskPriority priority;
}
