package ru.practicum.statsclient.exception;

public class ClientServerException extends RuntimeException {
    public ClientServerException(String message) {
        super(message);
    }
}
