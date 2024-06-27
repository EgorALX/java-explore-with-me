package ru.practicum.exception.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.model.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAccessException(final AccessException exception) {
        log.info("no access {}", exception.getMessage());
        return new ApiError(exception, exception.getMessage(), "no access", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException exception) {
        log.info("Validation exception {}", exception.getMessage());
        return new ApiError(exception, exception.getMessage(), "Validation exception", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleViolationException(final ViolationException exception) {
        log.info("Violation exception {}", exception.getMessage());
        return new ApiError(exception, exception.getMessage(), "Violation exception", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundEcxeption(final NotFoundException exception) {
        log.info("Data not found {}", exception.getMessage());
        return new ApiError(exception, exception.getMessage(), "Data not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(final Exception exception) {
        log.error("Exception: ", exception);
        return new ApiError(exception, exception.getMessage(), "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
