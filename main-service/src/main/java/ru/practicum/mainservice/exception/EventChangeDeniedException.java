package ru.practicum.mainservice.exception;

public class EventChangeDeniedException extends RuntimeException {
    public EventChangeDeniedException(String message) {
        super(message);
    }
}
