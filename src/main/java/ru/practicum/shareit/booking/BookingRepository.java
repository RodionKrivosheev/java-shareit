package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository  extends JpaRepository<Booking, Integer> {
    @Query("select b from Booking b where ( " +
            ":start <= b.start and b.start <= :end or " +
            ":start <= b.end and b.end <= :end) and " +
            "b.item.id = :itemId and " +
            "b.status = 'APPROVED'")
    List<Booking> findAllByDateAndId(int itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerId(int bookerId);

    List<Booking> findAllByBookerIdAndStatus(int bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndIsBefore(int bookerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStartIsAfter(int bookerId, LocalDateTime date);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.booker.id = :bookerId")
    List<Booking> findByBookerIdCurrDate(int bookerId, LocalDateTime date);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId")
    List<Booking> findAllItemBooking(int ownerId);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.end < :date")
    List<Booking> findAllItemBookingEndIsBefore(int ownerId, LocalDateTime date);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.start > :date")
    List<Booking> findAllItemBookingAndStartIsAfter(int ownerId, LocalDateTime date);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.item.owner.id = :ownerId")
    List<Booking> findAllItemBookingCurrDate(int ownerId, LocalDateTime date);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findAllItemBookingStatus(int ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(int itemId, LocalDateTime date);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStart(int itemId, LocalDateTime date);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, int itemId, LocalDateTime date);
}
