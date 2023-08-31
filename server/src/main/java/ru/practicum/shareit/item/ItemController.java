package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.constants.ConstReqHead.SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {


    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    ItemDto saveItem(@RequestHeader(SHARER_USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@PathVariable Long itemId, @RequestHeader(SHARER_USER_ID) Long userId,
                       @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(SHARER_USER_ID) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    List<ItemDto> getItemByUserId(@RequestHeader(SHARER_USER_ID) Long userId,
                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                  @RequestParam(defaultValue = "20") @Min(1) int size) {
        return itemService.getItemByUserId(userId, from, size);
    }

    @GetMapping("/search")
    List<ItemDto> getItemByText(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                @RequestParam(defaultValue = "20") @Min(1) int size) {
        return itemService.getItemByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(SHARER_USER_ID) Long userId, @PathVariable Long itemId,
                                  @RequestBody @Valid CommentDto commentDto) {
        return commentService.saveComment(itemId, userId, commentDto);
    }
}
