package com.aurora.core.model;

import com.aurora.metrics.MetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutionContext {
    private final Task task;
    private final MetricsCollector metricsCollector;

    public TaskExecutionContext(Task task, MetricsCollector metricsCollector) {
        this.task = task;
        this.metricsCollector = metricsCollector;
    }

    public Task getTask() {
        return task;
    }

    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }
}
