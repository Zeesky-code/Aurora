package com.aurora.core.model;

public interface TaskHandler {
    boolean execute(TaskExecutionContext context);
}
