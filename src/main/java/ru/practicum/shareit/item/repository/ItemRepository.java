package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item save(Item item);

    List<Item> findAllUserItems(long userId);

    Item update(long itemId, long userId, Item item);

    Item findById(long itemId);

    List<Item> search(String text);
}