package com.aurora.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import com.aurora.core.model.*;

public class MetricsCollector {
    private final MeterRegistry registry;
    private final Counter taskCompletedCounter;
    private final Counter taskFailedCounter;
    private final Timer taskExecutionTimer;

    public MetricsCollector(MeterRegistry registry) {
        this.registry = registry;
        this.taskCompletedCounter = Counter.builder("aurora.tasks.completed")
                .description("Number of completed tasks")
                .register(registry);
        this.taskFailedCounter = Counter.builder("aurora.tasks.failed")
                .description("Number of failed tasks")
                .register(registry);
        this.taskExecutionTimer = Timer.builder("aurora.tasks.execution.time")
                .description("Task execution time")
                .register(registry);
    }

    public void recordTaskCompletion(Task task) {
        taskCompletedCounter.increment();
    }

}