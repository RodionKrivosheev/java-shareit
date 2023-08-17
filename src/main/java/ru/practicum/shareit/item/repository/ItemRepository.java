package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
<<<<<<< HEAD
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
=======
>>>>>>> fa10711 (commit 1)
import ru.practicum.shareit.item.model.Item;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;
import java.util.stream.Collectors;
=======
>>>>>>> fa10711 (commit 1)

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items " +
            "WHERE available = TRUE AND " +
            "(LOWER(name) LIKE '%' || ?1 || '%' OR LOWER(description) LIKE '%' || ?1 || '%')",
            nativeQuery = true)
    List<Item> getItemByText(String text);

<<<<<<< HEAD
    private final Map<Integer, Item> items = new HashMap<>();

    private int id = 1;

    public int generateId() {
        return id++;
    }

    public Item saveItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    public Item getItemById(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public Item updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь не найдена!");
        }
        items.put(item.getId(), item);
        return item;
    }

    public List<Item> getItemByText(String text) {
        List<Item> result = new ArrayList<>();
        for (Item i : items.values()) {
            String name = i.getName().toLowerCase();
            String description = i.getDescription().toLowerCase();
            if ((name.contains(text) || description.contains(text)) && i.getAvailable()) {
                result.add(i);
            }
        }
        return result;
    }

    public List<ItemDto> getItemByUserId(int userId) {
        return getAllItems().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

=======
    List<Item> findAllByOwnerId(Long userId);
>>>>>>> fa10711 (commit 1)
}
