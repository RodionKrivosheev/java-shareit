package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostDto;

import java.util.List;

public interface BookingService {

    BookingDto create(int userId, PostDto postBookingDto);

    BookingDto findById(int userId, int bookingId);

    List<BookingDto> findUserBooking(int userId, String stateParam);

    List<BookingDto> findItemBooking(int userId, String stateParam);

    BookingDto approveBooking(int userId, int bookingId, Boolean approved);
}
