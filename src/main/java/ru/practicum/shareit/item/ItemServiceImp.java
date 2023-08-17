package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.PermissionException;
import ru.practicum.shareit.common.exception.UserCommentException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = UserMapper.mapToUser(userService.findById(userId));
        Item item = itemRepository.save(ItemMapper.mapToItem(owner, null, itemDto));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto findByItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> throwNotFoundException(itemId));
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemId, Sort.by("id"));
        itemDto.setComments(CommentMapper.mapToCommentDto(comments));

        Long ownerId = item.getOwner().getId();
        if (ownerId.equals(userId)) {
            loadBookingDates(itemDto);
        }

        return itemDto;
    }

    private void loadBookingDates(ItemDto itemDto) {
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemDto.getId(),
                LocalDateTime.now());
        itemDto.setLastBooking(mapToItemBookingDto(lastBooking));

        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStart(itemDto.getId(),
                LocalDateTime.now());
        itemDto.setNextBooking(mapToItemBookingDto(nextBooking));
    }

     private ItemBookingDto mapToItemBookingDto(Optional<Booking> booking) {
         if (booking.isPresent()) {
             Long id = booking.get().getId();
             Long bookerId = booking.get().getBooker().getId();
             LocalDateTime start = booking.get().getStart();
             LocalDateTime end = booking.get().getEnd();
             return new ItemBookingDto(id, bookerId, start, end);
         }
         return null;
    }

    @Override
    public List<ItemDto> findAllByUserID(Long userId) {
        userService.findById(userId);   //исключение, если пользователь не найден
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        items.sort(Comparator.comparing(Item::getId));
        List<ItemDto> dtoItems = ItemMapper.mapToItemDto(items);
        dtoItems.forEach(this::loadBookingDates);
        return dtoItems;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
       List<Item> items = itemRepository.search(text);

       return ItemMapper.mapToItemDto(items);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, PatchItemDto patchItemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> throwNotFoundException(itemId));
        checkPermissions(userId, item);

        if (patchItemDto.getName() != null) {
            item.setName(patchItemDto.getName());
        }
        if (patchItemDto.getDescription() != null) {
            item.setDescription(patchItemDto.getDescription());
        }
        if (patchItemDto.getAvailable() != null) {
            item.setAvailable(patchItemDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = UserMapper.mapToUser(userService.findById(userId));
        bookingRepository.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(
                () -> throwUserCommentException(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> throwNotFoundException(itemId));

        Comment comment = CommentMapper.mapToComment(author, item, commentDto, LocalDateTime.now());
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    public void checkPermissions(Long userId, Item item) {
        if (!Objects.equals(userId, item.getOwner().getId())) {
            String message = "Пользователь с id " + userId + " не владелец предмета с id " + item.getId();
            log.warn(message);
            throw new PermissionException(message);
        }
    }

    private NotFoundException throwNotFoundException(Long id) {
        String message = "Предмет с id " + id + " не найден!";
        log.warn(message);
        throw new NotFoundException(message);
    }

    private NotFoundException throwUserCommentException(Long id) {
        String message = "Пользователь с id " + id + "не имеет прав комментировать";
        log.warn(message);
        throw new UserCommentException(message);
    }
}
