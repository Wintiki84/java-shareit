package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private int counterId = 0;
    private final UserRepository userRepository;
    private Map<Long, List<Item>> items = new HashMap<>();
    private Map<Long, Item> storage = new LinkedHashMap<>();

    @Override
    public Item save(long userId, Item item) {
        item.setId(getId());
        item.setOwner(userRepository.findById(userId));

        items.compute(userId, (id, list) -> {
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
        item.setOwner(userRepository.findById(userId));

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
