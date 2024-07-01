package ru.practicum.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Getter
public class ApiError {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<String> errors;

    private String message;

    private String reason;

    private String status;

    private String timestamp;

    private String convertStackTrace(Exception exception) {
        StringWriter error = new StringWriter();
        exception.printStackTrace(new PrintWriter(error));
        String result = error.toString();
        return result;
    }

    public ApiError(Exception exception, String message, String reason, HttpStatus status) {
        this.errors = Collections.singletonList(convertStackTrace(exception));
        this.message = message;
        this.reason = reason;
        this.status = status.getReasonPhrase().toUpperCase();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }
}
