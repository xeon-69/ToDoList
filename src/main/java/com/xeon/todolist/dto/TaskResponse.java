package com.xeon.todolist.dto;

import com.xeon.todolist.enums.TaskPriority;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class TaskResponse {
    private Long taskId;
    private String title;
    private boolean isCompleted;
    private TaskPriority priority;
    private Integer priorityWeight;
    private long userId;
}
