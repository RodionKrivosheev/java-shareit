package ru.practicum.shareit.item.ItemStorage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    List<ItemDto> getItemByUserId(int userId);
}
