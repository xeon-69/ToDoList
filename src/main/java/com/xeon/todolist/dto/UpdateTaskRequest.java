package com.xeon.todolist.dto;


import com.xeon.todolist.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private boolean isCompleted;
    private TaskPriority priority;
}
