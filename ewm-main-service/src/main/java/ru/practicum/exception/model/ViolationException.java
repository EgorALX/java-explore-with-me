package ru.practicum.exception.model;

public class ViolationException extends RuntimeException {
    public ViolationException(String message) {
        super(message);
    }
}
