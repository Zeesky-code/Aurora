package com.aurora.core.model;

import java.time.Instant;

public class TaskStatusChange {
    private TaskStatus status;
    private Instant time;
    private String worker;

    public TaskStatusChange(TaskStatus newStatus, Instant now, String assignedWorker) {
        this.status = newStatus;
        this.time = now;
        this.worker = assignedWorker;
    }
}
