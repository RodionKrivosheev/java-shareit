package ru.practicum.shareit.exception;

public class ValidationExceptionHandler extends RuntimeException {
    public ValidationExceptionHandler(String message) {
        super(message);
    }
}
