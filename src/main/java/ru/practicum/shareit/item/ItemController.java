package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    public final String userIdMapping = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto create(@RequestHeader(userIdMapping) int userId,
                          @Valid @RequestBody ItemDto item) {
        return itemService.saveItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(userIdMapping) int userId, @PathVariable int itemId) {
        return itemService.findByItemId(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUserID(@RequestHeader(userIdMapping) int userId) {
        return itemService.findAllByUserID(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam  String text) {
        return itemService.search(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userIdMapping) int userId, @PathVariable int itemId,
                          @Valid @RequestBody PatchItemDto item) {
        return itemService.update(userId, itemId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(userIdMapping) int userId, @PathVariable int itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }

}
