package com.aurora.worker;

import com.aurora.core.config.AuroraConfig;
import com.aurora.core.exception.AuroraException;
import com.aurora.core.model.*;
import com.aurora.metrics.MetricsCollector;
import org.apache.curator.framework.CuratorFramework;

import java.time.Instant;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private final CuratorFramework curator;
    private final BlockingQueue<Task> taskQueue;
    private final ExecutorService executorService;
    private volatile boolean isRunning;
    //private final MetricsCollector metrics = new MetricsCollector();

    public TaskProcessor(CuratorFramework curator, BlockingQueue<Task> taskQueue) {
        this.curator = curator;
        this.taskQueue = taskQueue;
        this.executorService = Executors.newFixedThreadPool(
                AuroraConfig.getWorkerThreads()
        );
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
        //metricsCollector.recordStatusChange(task, newStatus);
    }

    private void handleTaskFailure(Task task, Exception e) {
        try {
            if (task.getRetryCount() < 3) {
                task.setRetryCount(task.getRetryCount() + 1);
                taskQueue.put(task);
            } else {
                updateTaskStatus(task, TaskStatus.FAILED);
                //metricsCollector.recordStatusChange(task, newStatus);
            }
        } catch (Exception ex) {
            throw new AuroraException("Failed to process task", e);
        }
    }
}