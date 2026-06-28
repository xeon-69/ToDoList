package com.xeon.todolist.exception;

public class TaskNotFoundException extends NotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
