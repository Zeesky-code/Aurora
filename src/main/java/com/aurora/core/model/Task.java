package com.aurora.core.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    private String id;
    private String name;
    private String payload;
    private TaskPriority priority;
    private TaskStatus status;
    private Instant createdAt;
    private Instant scheduledFor;
    private int retryCount;
    private int maxRetries;
    private String assignedWorker;
    private ArrayList<TaskStatusChange> statusHistory;

    public Task() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.status = TaskStatus.CREATED;
        this.maxRetries = 3;
        this.statusHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }


    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Instant scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public String getAssignedWorker() {
        return assignedWorker;
    }

    public void setAssignedWorker(String assignedWorker) {
        this.assignedWorker = assignedWorker;
    }

    public ArrayList<TaskStatusChange> getStatusHistory() {
        return statusHistory;
    }

}