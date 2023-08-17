package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    public final String userIdMapping = "X-Sharer-User-Id";

    @PostMapping
    ItemDto saveItem(@RequestHeader(userIdMapping) int userId, @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@PathVariable int itemId, @RequestHeader(userIdMapping) int userId,
                       @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    List<ItemDto> getItemByUserId(@RequestHeader(userIdMapping) int userId) {
        return itemService.getItemByUserId(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }
}
