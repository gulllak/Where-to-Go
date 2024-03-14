package ru.practicum.mainservice.model;

public enum RequestStatus {
    PENDING,

    CANCELED,

    REJECTED,

    CONFIRMED;

    public static RequestStatus from(String stateName) {
        for (RequestStatus state : values()) {
            if (state.name().equalsIgnoreCase(stateName)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown request status " + stateName);
    }
}
