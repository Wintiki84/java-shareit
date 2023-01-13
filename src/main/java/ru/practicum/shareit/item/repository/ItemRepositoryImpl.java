package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private int counterId = 0;
    private final Map<Long, List<Item>> items = new HashMap<>();
    private final Map<Long, Item> storage = new LinkedHashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.compute(item.getOwner().getId(), (id, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(item);
            return list;
        });
        storage.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> findAllUserItems(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item update(long itemId, long userId, Item item) {
        item.setId(itemId);
        items.get(userId).removeIf(i -> i.getId() == itemId);
        items.get(userId).add(item);
        storage.put(itemId, item);
        return item;
    }

    @Override
    public Item findById(long id) {
        return Optional.ofNullable(storage.get(id)).orElseThrow(() ->
                new NotFoundException(format("предмет с идентификатором: %d еще не существует", id)));
    }

    @Override
    public List<Item> search(String text) {
        return storage.values().stream()
                .filter(item -> {
                    if (item.getAvailable().equals(true)) {
                        return item.getName().toLowerCase().contains(text.toLowerCase())
                                || item.getDescription().toLowerCase().contains(text.toLowerCase());
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    private int getId() {
        return ++counterId;
    }
}
