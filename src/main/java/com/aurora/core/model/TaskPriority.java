package com.aurora.core.model;

public enum TaskPriority {
    LOW(0),
    MEDIUM(5),
    HIGH(10);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
