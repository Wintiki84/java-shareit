package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto save(Long userId, ItemDto itemDto);

    List<ItemDto> getAllUserItems(Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getById(long itemId, long userId);

    List<ItemDto> search(long userId, String text);
}