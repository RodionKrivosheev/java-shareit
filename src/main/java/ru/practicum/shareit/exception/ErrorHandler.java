package ru.practicum.shareit.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

<<<<<<< HEAD
import javax.validation.ValidationException;
import java.util.Objects;

=======
>>>>>>> fa10711 (commit 1)
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.error("Not Found Exception");
        return new ErrorResponse("Validation error 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidate(final ValidationException e) {
        log.error("Validation Exception");
        return new ErrorResponse("Validation error 400", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidate(final UserNotFoundException e) {
        log.error("User not found Exception");
        return new ErrorResponse("Validation error 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidate(final ErrorValidation e) {
        log.error("Validation Exception");
        return new ErrorResponse("Validation error 409", e.getMessage());
    }

<<<<<<< HEAD
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Throwable e) {
        String error = "Ошибка: " + e.getClass() + ", " + e.getMessage() + ", причина: " + Objects.toString(e.getCause().getMessage(), "");
        log.error(error);
        return new ErrorResponse(error);
=======
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidate(final BookingException e) {
        log.error("Validation Exception");
        return new ErrorResponse("Validation error 400", e.getMessage());
>>>>>>> fa10711 (commit 1)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidate(final BookingValidationException e) {
        log.error("Validation Exception");
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

}


