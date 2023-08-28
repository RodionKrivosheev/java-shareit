package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureWebMvc
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private final LocalDateTime start = LocalDateTime.of(2022, 11, 20, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2022, 11, 23, 10, 0);
    private final LocalDateTime past = LocalDateTime.of(2000, 11, 12, 1, 20);

    @BeforeEach
    void init() {

        UserDto userDto = UserDto
                .builder()
                .id(1L)
                .name("Roman")
                .email("roman@email.ru")
                .build();

        ItemDto itemDto = ItemDto
                .builder()
                .id(1L)
                .name("Молоток")
                .description("молоток забивной")
                .available(true)
                .build();

        bookingDto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10))
                .booker(new BookingDto.UserDtoShort(userDto.getId(), userDto.getName()))
                .item(new BookingDto.ItemDtoShort(itemDto.getId(), itemDto.getName()))
                .status(WAITING)
                .build();

        bookingRequestDto = BookingRequestDto
                .builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10))
                .itemId(itemDto.getId())
                .build();
    }

    @Test
    void testSaveBookingWithStatus200() throws Exception {
        when(bookingService.saveBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings/")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void testSaveBookingWithWrongStartWithStatus400() throws Exception {
        BookingRequestDto noStart = new BookingRequestDto(past, end, 1L);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(noStart))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveBookingWithWrongEndWithStatus400() throws Exception {
        BookingRequestDto noEnd = new BookingRequestDto(start, past, 1L);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(noEnd))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveBookingWithWrongItemWithStatus400() throws Exception {
        BookingRequestDto noItem = new BookingRequestDto(start, end, null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(noItem))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveBookingWithStatus400() throws Exception {
        when(bookingService.saveBooking(any(), anyLong()))
                .thenThrow(new BadRequestExceptionHandler("Validation error 400"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateBookingWithStatus200() throws Exception {
        bookingDto.setStatus(APPROVED);
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{id}?approved=true", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void testUpdateBookingWithStatus400() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new BadRequestExceptionHandler("Validation error 400"));

        mvc.perform(patch("/bookings/{id}?approved=true", 5)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error 400")));
    }

    @Test
    void testUpdateBookingWithStatus404() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("Validation error 404"));

        mvc.perform(patch("/bookings/{id}?approved=true", 5)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Validation error 404")));
    }

    @Test
    void testGetByIdBookingWithStatus200() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void testGetByIdBookingWithStatus404() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Validation error 404"));

        mvc.perform(get("/bookings/{id}", 5)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Validation error 404")));
    }

    @Test
    void testGetAllByBookerWithStatus200() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings?state=WAITING&from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetAllByBookerWithWrongFromWithStatus400() throws Exception {
        mvc.perform(get("/bookings?state=WAITING&from=-2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByBookerWithWrongSizeWithStatus400() throws Exception {
        mvc.perform(get("/bookings?state=WAITING&from=2&size=0")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByBookerWithStatus400() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new BadRequestExceptionHandler("Validation error 400"));

        mvc.perform(get("/bookings?state=WAITING&from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error 400")));
    }

    @Test
    void testGetAllByBookerWithStatus404() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Validation error 404"));

        mvc.perform(get("/bookings?state=WAITING&from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Validation error 404")));
    }

    @Test
    void testGetAllByOwnerWithStatus200() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner?state=WAITING&from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetAllByOwnerWithWrongFromWithStatus400() throws Exception {
        mvc.perform(get("/bookings/owner?state=WAITING&from=-2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByOwnerWithWrongSizeWithStatus400() throws Exception {
        mvc.perform(get("/bookings/owner?state=WAITING&from=2&size=0")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByOwnerWithStatus400() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new BadRequestExceptionHandler("Validation error 400"));

        mvc.perform(get("/bookings/owner?state=WAITING&from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error 400")));
    }

    @Test
    void testGetAllByOwnerWithStatus404() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Validation error 404"));

        mvc.perform(get("/bookings/owner?state=WAITING&from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Validation error 404")));
    }
}
