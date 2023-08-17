package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto create(int userId, PostDto postBookingDto) {
        User booker = UserMapper.mapToUser(userService.findById(userId));
        int bookerId = booker.getId();
        int itemId = postBookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> throwNotFoundItemException("Предмет с id " +
                itemId + " не найден!"));
        checkItemAvailable(item);
        Booking booking = BookingMapper.mapToBooking(booker, item, postBookingDto, BookingStatus.WAITING);
        checkBookingDate(booking);
        checkBookingAvailable(booking);

        if (Objects.equals(bookerId, item.getOwner().getId())) {
            String message = "Предмет " + itemId + " не доступен для бронирования владельцем " + bookerId;
            log.warn(message);
            throw new PermissionException(message);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> throwNotFoundItemException(
                "Бронирование с id " + bookingId + " не найдено!"));

        int bookerId = booking.getBooker().getId();
        int ownerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, bookerId) && !Objects.equals(userId, ownerId)) {
            String message = "У пользователя " + userId + " нет прав на просмотр бронирования " + bookingId;
            log.warn(message);
            throw new PermissionException(message);
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findUserBooking(int userId, String stateParam) {
        BookingState state = stateToEnum(stateParam);
        userService.findById(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrDate(userId,  LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingDto> findItemBooking(int userId, String stateParam) {
        BookingState state = stateToEnum(stateParam);
        userService.findById(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllItemBooking(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllItemBookingEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllItemBookingAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllItemBookingCurrDate(userId,  LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllItemBookingStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllItemBookingStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Transactional
    @Override
    public BookingDto approveBooking(int userId, int bookingId, Boolean approved) {
        userService.findById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> throwNotFoundItemException(
                "Бронирование с id " + bookingId + " не найдено!"));
        itemService.checkPermissions(userId, booking.getItem());
        checkBookingStatus(booking);
        BookingStatus status = (approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking.setStatus(status);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    private NotFoundException throwNotFoundItemException(String message) {
        log.warn(message);
        throw new NotFoundException(message);
    }

    private void checkItemAvailable(Item item) {
        if (!item.isAvailable()) {
            String message = "Предмет " + item.getId() + " не доступен для бронирования";
            log.warn(message);
            throw new ItemNotAvailableException(message);
        }
    }

    private void checkBookingDate(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getStart().isAfter(booking.getEnd())) {
            String message = "Некорректная дата бронирования";
            log.warn(message);
            throw new IncorrectDateException(message);
        }
    }

    private void checkBookingStatus(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            String message = "Бронирование уже одобрено";
            log.warn(message);
            throw new BookingAlreadyApproveException(message);
        }
    }

    private void checkBookingAvailable(Booking booking) {
        int itemId = booking.getItem().getId();
        List<Booking> bookings = bookingRepository.findAllByDateAndId(itemId, booking.getStart(), booking.getEnd());
        if (bookings.size() != 0) {
            String message = "Товар " + itemId + " не доступен для бронирования";
            log.warn(message);
            throw new ItemNotAvailableException(message);
        }
    }

    private BookingState stateToEnum(String stateParam) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            String message = "Unknown state: UNSUPPORTED_STATUS";
            log.warn(message);
            throw new StateIsNotSupportException(message);
        }
        return state;
    }
}
