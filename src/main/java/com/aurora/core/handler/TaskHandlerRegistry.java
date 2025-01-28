package com.aurora.core.handler;

import com.aurora.core.model.TaskHandler;

import java.util.HashMap;
import java.util.Map;

public class TaskHandlerRegistry {
    private final Map<String, TaskHandler> handlers = new HashMap<>();

    public TaskHandlerRegistry() {
    }

    public void registerHandler(String taskName, TaskHandler handler) {
        handlers.put(taskName, handler);
    }

    public TaskHandler getHandler(String taskName) {
        return handlers.get(taskName);
    }
}
