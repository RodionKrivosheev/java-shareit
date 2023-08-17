package ru.practicum.shareit.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingAlreadyApproveException extends RuntimeException {
    public BookingAlreadyApproveException(String message) {
        super(message);
    }
}