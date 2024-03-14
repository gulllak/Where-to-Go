package ru.practicum.mainservice.exception;

public class UpdateValidationException extends RuntimeException {
    public UpdateValidationException(String message) {
        super(message);
    }
}
