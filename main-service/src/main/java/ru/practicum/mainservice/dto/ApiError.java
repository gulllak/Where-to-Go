package ru.practicum.mainservice.dto;

public class ApiError {
    private final String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
