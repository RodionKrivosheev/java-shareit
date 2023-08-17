package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(int userId, ItemDto itemDto);

    ItemDto findByItemId(int userId, int itemId);

    List<ItemDto> findAllByUserID(int userId);

    List<ItemDto> search(String text);

    ItemDto update(int userId, int itemId, PatchItemDto patchItemDto);

    void checkPermissions(int userId, Item item);

    CommentDto createComment(int userId, int itemId, CommentDto commentDto);
}
