package ru.practicum.shareit.item.ItemStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemStorageImpl implements ItemStorage{

    private final ItemRepository itemRepository;

    public ItemStorageImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    @Override
    public List<ItemDto> getItemByUserId(int userId) {
        return itemRepository.getAllItems().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
