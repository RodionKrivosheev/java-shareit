package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final String sharerUserId = "X-Sharer-User-Id";

    @PostMapping
    BookingDto saveBooking(@RequestHeader(sharerUserId) Long userId, @RequestBody @Valid BookingRequestDto bookingDto) {
        return bookingService.saveBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(sharerUserId) Long userId, @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(sharerUserId) Long userId, @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader(sharerUserId) Long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(defaultValue = "0") @Min(0) int from,
                                           @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(sharerUserId) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestParam(defaultValue = "0") @Min(0) int from,
                                          @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}
