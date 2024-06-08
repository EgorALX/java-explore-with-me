package ru.practicum;

import lombok.Getter;

@Getter
public class ErrorDto {

    private final String error;

    private String stackTrace = "";

    public ErrorDto(String message) {
        this.error = message;
    }

    public ErrorDto(String message, String stackTrace) {
        this.error = message;
        this.stackTrace = stackTrace;
    }
}
