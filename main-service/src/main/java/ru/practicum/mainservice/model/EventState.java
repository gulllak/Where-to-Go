package ru.practicum.mainservice.model;

public enum EventState {
    PENDING,

    PUBLISHED,

    CANCELED;

    public static EventState from(String stateName) {
        for (EventState state : values()) {
            if (state.name().equalsIgnoreCase(stateName)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown event state " + stateName);
    }
}
