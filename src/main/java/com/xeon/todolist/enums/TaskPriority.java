package com.xeon.todolist.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TaskPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int weight;

}
