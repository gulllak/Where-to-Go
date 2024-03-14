package ru.practicum.mainservice.model;

public enum CommentStatus {
    PENDING,

    CANCELED,

    REJECTED,

    CONFIRMED;

    public static CommentStatus from(String stateName) {
        for (CommentStatus state : values()) {
            if (state.name().equalsIgnoreCase(stateName)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown request status " + stateName);
    }
}
