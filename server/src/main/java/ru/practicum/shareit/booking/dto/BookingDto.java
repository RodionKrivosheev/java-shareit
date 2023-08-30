package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.constants.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShortDto itemShortDto;
    private UserShortDto booker;
    private Status status;

    @Data
    public static class ItemShortDto {
        private final Long id;
        private final String name;
    }

    @Data
    public static class UserShortDto {
        private final Long id;
        private final String name;
    }
}
