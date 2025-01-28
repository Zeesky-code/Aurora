package com.aurora.scheduler;

import com.aurora.core.model.*;
import org.apache.curator.framework.CuratorFramework;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private final CuratorFramework curator;
    private final String zkPath = "/scheduler";

    public Scheduler(String zkConnectString) {
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(zkConnectString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
    }

    public void scheduleTask(Task task) throws Exception {
        String taskPath = zkPath + "/tasks/" + task.getId();
        curator.create()
                .creatingParentsIfNeeded()
                .forPath(taskPath, serializeTask(task));

        notifyWorkers(task);
    }
    
    private void notifyWorkers(Task task) throws Exception {
        String notificationPath = zkPath + "/notifications/" + task.getId();
        curator.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(notificationPath);
    }
    private byte[] serializeTask(Task task) throws Exception {
        return new ObjectMapper().writeValueAsBytes(task);
    }

}
