package com.xeon.todolist.dto;

import com.xeon.todolist.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {

    private String title;
    private TaskPriority priority;
}
