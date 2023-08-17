package ru.practicum.shareit.exception;

public class ErrorValidation extends RuntimeException {
    public ErrorValidation(final String message) {
        super(message);
    }
}
