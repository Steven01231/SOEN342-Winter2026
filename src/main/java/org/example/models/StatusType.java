package org.example.models;

public enum StatusType {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    DONE;

    public boolean isFinal() {
        return this == DONE;
    }

    public boolean isActive() {
        return this == IN_PROGRESS || this == BLOCKED;
    }
}
