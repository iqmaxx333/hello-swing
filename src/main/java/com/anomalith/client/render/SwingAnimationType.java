package com.anomalith.client.render;

public enum SwingAnimationType {
    CLASSIC("Classic"),
    SMOOTH("Smooth"),
    DYNAMIC("Dynamic"),
    REALISTIC("Realistic");

    private final String displayName;

    SwingAnimationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SwingAnimationType next() {
        int nextIndex = (this.ordinal() + 1) % values().length;
        return values()[nextIndex];
    }

    public SwingAnimationType previous() {
        int prevIndex = (this.ordinal() - 1 + values().length) % values().length;
        return values()[prevIndex];
    }
}
