package com.aurora.core.handler;

import com.aurora.core.model.TaskExecutionContext;
import com.aurora.core.model.TaskHandler;

public class DataProcessingTaskHandler implements TaskHandler {
    @Override
    public boolean execute(TaskExecutionContext context) {
        context.getLogger().info("Executing data processing task: " + context.getTask().getId());
        return true;
    }
}
