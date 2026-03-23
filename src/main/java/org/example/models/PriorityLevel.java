package org.example.models;

public enum PriorityLevel {
    LOW(0),
    MEDIUM(1),
    HIGH(2),
    CRITICAL(3),
    URGENT(4);

    private final int level;


    PriorityLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static PriorityLevel fromValue(int value) {
        for (PriorityLevel p : values()) {
            if (p.level == value) return p;
        }
        throw new IllegalArgumentException("Invalid PriorityLevel: " + value);
    }
}
