package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Column(name = "start_date")
    private  LocalDateTime start;    //дата и время начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end;      //дата и время конца бронирования

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;              //вещь, которую бронируют

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User booker;            //пользователь, который бронирует

    @Enumerated(EnumType.STRING)
    private BookingStatus status;   //статус бронирования

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
