package ru.practicum.shareit.exception;

public class BadRequestExceptionHandler extends RuntimeException {

    public BadRequestExceptionHandler(String message) {
        super(message);
    }
}
