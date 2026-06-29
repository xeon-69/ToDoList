package com.xeon.todolist.exception;

public class TaskAccessDeniedException extends AccessDeniedException {
    public TaskAccessDeniedException(String message) {
        super(message);
    }
}
