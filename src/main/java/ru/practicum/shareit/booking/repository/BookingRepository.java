package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.constant.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByIdDesc(Long userId, Pageable page);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfterOrderByIdDesc(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdDesc(Long userId, LocalDateTime now,
                                                                          LocalDateTime now1, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByIdDesc(Long userId, Status status, Pageable page);

    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long bookerId, Pageable page);

    List<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now,
                                                                           LocalDateTime now1, Pageable page);

    List<Booking> findBookingsByItem_Owner_IdAndStatusEquals(Long bookerId, Status status, Pageable page);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status approved,
                                                                          LocalDateTime now);

    List<Booking> findBookingsByItem(Item item);
}
