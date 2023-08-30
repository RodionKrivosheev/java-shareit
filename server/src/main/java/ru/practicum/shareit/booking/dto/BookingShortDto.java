package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.constants.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingShortDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;
    private Long bookerId;

    private Status status;
}
