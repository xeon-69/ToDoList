package com.xeon.todolist.exception;

public class DuplicateTaskException extends RuntimeException {
  public DuplicateTaskException(String message) {
    super(message);
  }

  public DuplicateTaskException(String message, Throwable cause) {
    super(message, cause);
  }
}
