package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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
    List<Booking> findAllByBookerIdOrderByIdDesc(Long userId, Sort sort);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfterOrderByIdDesc(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderById(Long userId, LocalDateTime now,
                                                                      LocalDateTime now1, Sort sort);

    List<Booking> findAllByBookerIdAndStatusOrderById(Long userId, Status status, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long bookerId, Sort sort);

    //PAST
    List<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now,
                                                                           LocalDateTime now1, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStatusEquals(Long bookerId, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status approved);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(Long itemId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status approved,
                                                                          LocalDateTime now);

    List<Booking> findBookingsByItem(Item item);

    List<Booking> findBookingsByItemInAndStatus(List<Item> items, Status status, Sort sort);
}
