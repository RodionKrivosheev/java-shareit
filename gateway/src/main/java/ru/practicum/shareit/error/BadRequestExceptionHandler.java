package ru.practicum.shareit.error;

public class BadRequestExceptionHandler extends RuntimeException {

    public BadRequestExceptionHandler(String message) {
        super(message);
    }
}
