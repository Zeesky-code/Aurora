package com.aurora.worker;

import com.aurora.core.config.AuroraConfig;
import com.aurora.core.exception.AuroraException;
import com.aurora.core.handler.TaskHandlerRegistry;
import com.aurora.core.model.*;
import com.aurora.metrics.MetricsCollector;
import org.apache.curator.framework.CuratorFramework;

import java.time.Instant;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    private final TaskHandlerRegistry taskHandlerRegistry= new TaskHandlerRegistry();
    private final MetricsCollector metricsCollector;
    private final CuratorFramework curator;
    private final BlockingQueue<Task> taskQueue;
    private final ExecutorService executorService;
    private volatile boolean isRunning;

    public TaskProcessor(CuratorFramework curator, BlockingQueue<Task> taskQueue, MetricsCollector metricsCollector) {
        this.curator = curator;
        this.taskQueue = taskQueue;
        this.executorService = Executors.newFixedThreadPool(
                AuroraConfig.getWorkerThreads()
        );
        this.metricsCollector = metricsCollector;
    }

    public void start() {
        isRunning = true;
        executorService.submit(this::processTasksLoop);
    }

    private void processTasksLoop() {
        while (isRunning) {
            try {
                Task task = taskQueue.poll(1, TimeUnit.SECONDS);
                if (task != null) {
                    processTask(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processTask(Task task) {
        try {
            updateTaskStatus(task, TaskStatus.RUNNING);
            //executeTask(task);
            updateTaskStatus(task, TaskStatus.COMPLETED);
            metricsCollector.recordTaskCompletion(task);
        } catch (Exception e) {
            handleTaskFailure(task, e);
        }
    }
    private void updateTaskStatus(Task task, TaskStatus newStatus) throws Exception {
        String taskPath = "/aurora/tasks/" + task.getId();
        task.setStatus(newStatus);

        TaskStatusChange statusChange = new TaskStatusChange(
                newStatus,
                Instant.now(),
                task.getAssignedWorker()
        );
        task.getStatusHistory().add(statusChange);
    }

    private void handleTaskFailure(Task task, Exception e) {
        try {
            if (task.getRetryCount() < 3) {
                task.setRetryCount(task.getRetryCount() + 1);
                taskQueue.put(task);
            } else {
                updateTaskStatus(task, TaskStatus.FAILED);
                metricsCollector.recordTaskFailure(task);
            }
        } catch (Exception ex) {
            throw new AuroraException("Failed to process task", e);
        }
    }

    private void executeTask(Task task) throws Exception {
        // Get task handler based on task type
        TaskHandler handler = taskHandlerRegistry.getHandler(task.getName());
        if (handler == null) {
            throw new AuroraException("No handler found for task type: " + task.getName());
        }
        TaskExecutionContext context = new TaskExecutionContext(
                task,
                metricsCollector
        );

        // Execute with timeout
        try {
            CompletableFuture.supplyAsync(() -> {
                return handler.execute(context);
            }).get(2, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            throw new AuroraException("Task execution timed out", e);
        }
    }
}