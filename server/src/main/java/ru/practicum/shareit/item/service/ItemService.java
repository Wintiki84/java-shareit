package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ItemService {

    @NotNull
    ItemDto save(@NotNull Long userId, @NotNull ItemDto itemDto);

    @NotNull
    List<ItemDto> findAllByOwnerId(Long ownerId, int from, int size);

    @NotNull
    ItemDto update(@NotNull Long itemId, @NotNull Long userId, @NotNull ItemDto itemDto);

    @NotNull
    ItemDto getById(@NotNull Long itemId, @NotNull Long userId);

    @NotNull
    List<ItemDto> findAllByText(String text, int from, int size);

    @NotNull
    CommentDto saveComment(@NotNull Long userId, @NotNull Long itemId, @NotNull CommentDto commentDto);
}