package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.Status.APPROVED;
import static ru.practicum.shareit.item.ItemMapper.*;
import static ru.practicum.shareit.item.comment.service.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.comment.service.CommentMapper.toCommentsDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User savedUser = getUser(userId);
        Item item = toItem(itemDto);
        item.setOwner(savedUser);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = getItemRequest(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        log.info("Вещь добавлена.");
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = getItem(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Неверный ID пользователя.");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Данные вещи обновлены.");
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {

        Item item = getItem(itemId);
        ItemDto itemDto = toItemDto(item);
        populateItemDto(itemDto);
        if (!item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemByUserId(Long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.findAllByOwnerId(userId, page).stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());

        List<ItemDto> itemsDto = this.setBookings(items);
        this.setComments(itemsDto);
        log.info("Получен список всех вещей пользователя.");
        return itemsDto;
    }

    @Override
    public List<ItemDto> getItemByText(String text, int from, int size) {

        if (text.isBlank()) {
            return List.of();
        }
        String query = text.toLowerCase();
        Pageable page = PageRequest.of(from / size, size);
        Page<Item> items = itemRepository.getItemByText(query, page);
        if (items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Неверный ID."));
    }

    private void populateItemDto(ItemDto itemDto) {
        BookingShortDto lastBooking = getLastBooking(itemDto.getId());
        BookingShortDto nextBooking = getNextBooking(itemDto.getId());
        List<CommentDto> comments = toCommentsDto(commentRepository.findAllByItemId(itemDto.getId()));

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);

        itemDto.setComments(comments);
    }

    private BookingShortDto getNextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), APPROVED).map(BookingMapper::toBookingShortDto).orElse(null);
    }

    private BookingShortDto getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(itemId, LocalDateTime.now()).map(BookingMapper::toBookingShortDto).orElse(null);
    }

    private void setComments(List<ItemDto> items) {
        Map<Long, ItemDto> itemsDto = new HashMap<>();
        items.forEach(item -> itemsDto.put(item.getId(), item));

        Set<Comment> comments = new HashSet<>(commentRepository.findCommentsByItemId(itemsDto.keySet()));

        if (!itemsDto.isEmpty()) {
            comments.forEach(comment -> Optional.ofNullable(itemsDto.get(comment.getItem().getId())).ifPresent(i -> i.getComments().add(toCommentDto(comment))));
        }
    }

    private List<ItemDto> setBookings(List<Item> items) {
        List<ItemDto> itemsDto = toItemsDto(items);
        Map<Long, List<Booking>> bookings = bookingRepository.findBookingsByItemInAndStatus(items, APPROVED, Sort.by(Sort.Direction.ASC, "start")).stream().collect(Collectors.groupingBy(b -> b.getItem().getId(), Collectors.toList()));

        if (!itemsDto.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            itemsDto.forEach(item -> {

                Optional<Booking> nextBooking = bookings.getOrDefault(item.getId(), Collections.emptyList()).stream().filter(b -> b.getStart().isAfter(now)).findFirst();
                Optional<Booking> lastBooking = bookings.getOrDefault(item.getId(), Collections.emptyList()).stream().filter(b -> !b.getStart().isAfter(now)).reduce((first, second) -> second);

                item.setNextBooking(nextBooking.map(BookingMapper::toBookingShortDto).orElse(null));
                item.setLastBooking(lastBooking.map(BookingMapper::toBookingShortDto).orElse(null));
            });
        }
        return itemsDto;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный ID пользователя."));
    }

    private ItemRequest getItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Неверный ID запроса."));
    }
}
