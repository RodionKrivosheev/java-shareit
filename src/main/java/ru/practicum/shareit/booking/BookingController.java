package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    public final String userIdMapping = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(userIdMapping) int userId,
                             @Valid @RequestBody PostDto postBookingDto) {
        return bookingService.create(userId, postBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(userIdMapping) int userId, @PathVariable int bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findUserBooking(@RequestHeader(userIdMapping) int userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam) {
        return bookingService.findUserBooking(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemBooking(@RequestHeader(userIdMapping) int userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam) {
        return bookingService.findItemBooking(userId, stateParam);
    }

    @PatchMapping("/{bookingId}")
    public  BookingDto approveRequest(@RequestHeader(userIdMapping) int userId, @PathVariable int bookingId,
                                      @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }
}
