package com.aurora.worker;

import com.aurora.core.config.AuroraConfig;
import com.aurora.core.exception.AuroraException;
import com.aurora.core.model.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import java.util.concurrent.*;

import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private final String workerId;
    private final CuratorFramework curator;
    private final LeaderLatch leaderLatch;
    private final ExecutorService taskExecutor;
    private final BlockingQueue<Task> taskQueue;
    private final TaskProcessor taskProcessor;
    private volatile boolean isRunning;

    public Worker(String workerId, String zkConnectString) {
        this.workerId = workerId;
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(zkConnectString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.leaderLatch = new LeaderLatch(curator, "/aurora/leader");
        this.taskExecutor = Executors.newFixedThreadPool(
                AuroraConfig.getWorkerThreads()
        );
        this.taskQueue = new PriorityBlockingQueue<>(
                1000,
                (t1, t2) -> t2.getPriority().getValue() - t1.getPriority().getValue()
        );
        this.taskProcessor = new TaskProcessor(curator, taskQueue);
    }

    private void registerWorker() {
        try {
            String workerPath = "/scheduler/workers/" + workerId;
            curator.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(workerPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register worker", e);
        }
    }

    public void start() {
        logger.info("Starting worker: {}", workerId);
        curator.start();
        try {
            leaderLatch.start();
            registerWorker();
            startTaskProcessing();
        } catch (Exception e) {
            throw new AuroraException("Failed to start worker", e);
        }
    }
    public void stop() {
        isRunning = false;
        taskExecutor.shutdown();
        try {
            if (!taskExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            taskExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Worker stopped: {}", workerId);
    }


    private void startTaskProcessing() {
        isRunning = true;
        taskProcessor.start();
    }
}