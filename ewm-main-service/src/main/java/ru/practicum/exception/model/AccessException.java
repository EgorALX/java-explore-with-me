package ru.practicum.exception.model;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }
}
