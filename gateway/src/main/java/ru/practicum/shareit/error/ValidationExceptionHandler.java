package ru.practicum.shareit.error;

public class ValidationExceptionHandler extends RuntimeException {
    public ValidationExceptionHandler(String message) {
        super(message);
    }
}
