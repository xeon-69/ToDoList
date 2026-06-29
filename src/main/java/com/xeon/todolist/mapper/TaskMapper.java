package com.xeon.todolist.mapper;

import com.xeon.todolist.dto.TaskResponse;
import com.xeon.todolist.entity.Tasks;
import org.springframework.stereotype.Component;

@Component // to allow injection
public class TaskMapper {
    public TaskResponse toResponse(Tasks task){
        return TaskResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .isCompleted(task.isCompleted())
                .priority(task.getPriority())
                .priorityWeight(task.getPriorityWeight())
                .userId(task.getUser().getId())
                .build();
    }

}
