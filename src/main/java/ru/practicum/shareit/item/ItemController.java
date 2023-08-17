package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

<<<<<<< HEAD
    public final String userIdMapping = "X-Sharer-User-Id";

    @PostMapping
    ItemDto saveItem(@RequestHeader(userIdMapping) int userId, @RequestBody ItemDto itemDto) {
=======
    private final CommentService commentService;

    public final String userIdMapping = "X-Sharer-User-Id";

    @PostMapping
    ItemDto saveItem(@RequestHeader(userIdMapping) Long userId, @Valid @RequestBody ItemDto itemDto) {
>>>>>>> fa10711 (commit 1)
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
<<<<<<< HEAD
    ItemDto updateItem(@PathVariable int itemId, @RequestHeader(userIdMapping) int userId,
=======
    ItemDto updateItem(@PathVariable Long itemId, @RequestHeader(userIdMapping) Long userId,
>>>>>>> fa10711 (commit 1)
                       @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(userIdMapping) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
<<<<<<< HEAD
    List<ItemDto> getItemByUserId(@RequestHeader(userIdMapping) int userId) {
=======
    List<ItemDto> getItemByUserId(@RequestHeader(userIdMapping) Long userId) {
>>>>>>> fa10711 (commit 1)
        return itemService.getItemByUserId(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(userIdMapping) Long userId, @PathVariable Long itemId,
                                  @RequestBody @Valid CommentDto commentDto) {
        return commentService.saveComment(itemId, userId, commentDto);
    }
}
